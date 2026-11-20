import hashlib
import hmac
import json
import sys
import time
import urllib.error
import urllib.request

SECRET = "local-dev-shared-secret"
URL = "http://localhost:8080/webhooks/payment"

if len(sys.argv) < 3:
    print("Usage: python pay.py <qr_code_id> <amount_in_rupees> [transaction_id]")
    sys.exit(1)

qr_id = sys.argv[1]
amount_rupees = float(sys.argv[2])
txn_id = sys.argv[3] if len(sys.argv) > 3 else f"pay_mock_{int(time.time() * 1000)}"

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
    URL,
    data=body.encode(),
    method="POST",
    headers={"Content-Type": "application/json", "X-Razorpay-Signature": signature},
)

try:
    with urllib.request.urlopen(request) as response:
        print(response.status, response.read().decode())
except urllib.error.HTTPError as e:
    print(e.code, e.read().decode())