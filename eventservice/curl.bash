for i in {1..5000}; do
  curl -i http://localhost:8765/events/account?accountId=1289
done