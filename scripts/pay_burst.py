import hashlib
import hmac
import json
import sys
import threading
import time
import urllib.error
import urllib.request

SECRET = "local-dev-shared-secret"
URL = "http://localhost:8080/webhooks/payment"


def send(qr_id, amount_rupees, txn_id):
    body = json.dumps({
        "event": "qr_code.credited",
        "payload": {
            "payment": {"entity": {
                "id": txn_id,
                "amount": int(round(amount_rupees * 100)),
                "vpa": "student@okhdfcbank",
                "email": "student@example.com",
            }},
            "qr_code": {"entity": {"id": qr_id}},
        },
    }, separators=(",", ":"))
    signature = hmac.new(SECRET.encode(), body.encode(), hashlib.sha256).hexdigest()
    request = urllib.request.Request(
        URL, data=body.encode(), method="POST",
        headers={"Content-Type": "application/json", "X-Razorpay-Signature": signature},
    )
    try:
        with urllib.request.urlopen(request) as response:
            return response.status
    except urllib.error.HTTPError as e:
        return e.code


if len(sys.argv) < 4:
    print("Usage: python pay_burst.py <qr_code_id> <amount_in_rupees> <count>")
    sys.exit(1)

qr_id = sys.argv[1]
amount = float(sys.argv[2])
count = int(sys.argv[3])
run_id = int(time.time() * 1000)

barrier = threading.Barrier(count)
results = [None] * count


def worker(i):
    barrier.wait()  # every thread starts at the same moment
    results[i] = send(qr_id, amount, f"pay_burst_{run_id}_{i}")


threads = [threading.Thread(target=worker, args=(i,)) for i in range(count)]
for t in threads:
    t.start()
for t in threads:
    t.join()
print(results)