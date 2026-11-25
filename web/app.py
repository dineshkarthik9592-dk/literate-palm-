import json
import os
import sys
import time
from http.server import HTTPServer, SimpleHTTPRequestHandler
from urllib.parse import urlparse, parse_qs

# In-memory database & simulation engine mirroring Java backend
products = [
    {"productId": "PROD-001", "name": "Amoxicillin 500mg Capsules", "category": "Antibiotic", "description": "500mg oral capsules"}
]

batches = [
    {"batchId": "BATCH-101", "productId": "PROD-001", "manufactureDate": "2026-08-15", "expiryDate": "2027-08-15", "status": "ACTIVE", "totalQuantity": 10000}
]

organizations = {
    "M1": {"id": "M1", "name": "Apex Pharmaceuticals Facility", "type": "MANUFACTURER", "location": "Hyderabad, TS", "contact": "+91-40-23001122"},
    "W1": {"id": "W1", "name": "Central Pharma Hub Guntur", "type": "WAREHOUSE", "location": "Guntur, AP", "contact": "+91-863-221100"},
    "W2": {"id": "W2", "name": "Coastal Depot Vijayawada", "type": "WAREHOUSE", "location": "Vijayawada, AP", "contact": "+91-866-234567"},
    "D1": {"id": "D1", "name": "MedLife Distributors Amaravati", "type": "DISTRIBUTOR", "location": "Amaravati, AP", "contact": "+91-863-445566"},
    "D2": {"id": "D2", "name": "Vikas Wholesale Tirupati", "type": "DISTRIBUTOR", "location": "Tirupati, AP", "contact": "+91-877-556677"},
    "R1": {"id": "R1", "name": "Apollo Pharmacy Store #12", "type": "RETAILER", "location": "Guntur, AP", "contact": "+91-863-998877"},
    "R2": {"id": "R2", "name": "MedPlus Chemist #45", "type": "RETAILER", "location": "Vijayawada, AP", "contact": "+91-866-778899"},
    "R3": {"id": "R3", "name": "City Care Health Mart", "type": "RETAILER", "location": "Mangalagiri, AP", "contact": "+91-8645-334455"}
}

shipments = [
    {"id": "SHP-001", "batchId": "BATCH-101", "from": "M1", "to": "W1", "qty": 10000, "date": "2026-08-17"},
    {"id": "SHP-002", "batchId": "BATCH-101", "from": "W1", "to": "D1", "qty": 5000, "date": "2026-08-20"},
    {"id": "SHP-003", "batchId": "BATCH-101", "from": "W1", "to": "D2", "qty": 4000, "date": "2026-08-21"},
    {"id": "SHP-004", "batchId": "BATCH-101", "from": "D1", "to": "R1", "qty": 3000, "date": "2026-08-23"},
    {"id": "SHP-005", "batchId": "BATCH-101", "from": "D1", "to": "R2", "qty": 2000, "date": "2026-08-24"},
    {"id": "SHP-006", "batchId": "BATCH-101", "from": "D2", "to": "R3", "qty": 2500, "date": "2026-08-25"}
]

incidents = [
    {
        "incidentId": "INC-301",
        "batchId": "BATCH-101",
        "date": "2026-11-18 09:30:00",
        "description": "Particulate discoloration observed during shelf inspection at regional dispensary.",
        "reporter": "QA_INSPECTOR_04",
        "status": "OPEN"
    }
]

recalls = {}
recall_responses = {}
audit_events = [
    {"id": "EVT-1001", "user": "SYS_ADMIN", "type": "BATCH", "entity": "BATCH-101", "action": "REGISTER", "time": "2026-11-18 08:00:00", "notes": "Batch BATCH-101 registered in master database."},
    {"id": "EVT-1002", "user": "QA_INSPECTOR_04", "type": "INCIDENT", "entity": "INC-301", "action": "REPORT", "time": "2026-11-18 09:30:00", "notes": "Quality incident INC-301 reported on BATCH-101."}
]
audit_counter = 1002

def log_audit(user, entity_type, entity_id, action, notes):
    global audit_counter
    audit_counter += 1
    event = {
        "id": f"EVT-{audit_counter}",
        "user": user,
        "type": entity_type,
        "entity": entity_id,
        "action": action,
        "time": time.strftime("%Y-%m-%d %H:%M:%S"),
        "notes": notes
    }
    audit_events.append(event)
    return event

# BFS Traversal Algorithm matching Java TraceabilityService
def run_bfs(batch_id, start_org_id):
    start_time = time.time()
    # Build adjacency list for batch
    adj = {k: [] for k in organizations}
    for s in shipments:
        if s["batchId"].lower() == batch_id.lower():
            if s["from"] not in adj:
                adj[s["from"]] = []
            adj[s["from"]].append(s)

    queue = [start_org_id]
    visited = {start_org_id}
    paths = {start_org_id: [start_org_id]}
    distances = {start_org_id: 0}
    quantities = {}
    traversal_steps = [f"Initiated BFS at origin node [{start_org_id}] for batch '{batch_id}'."]
    discovered_order = []

    while queue:
        current = queue.pop(0)
        curr_dist = distances[current]
        outgoing = adj.get(current, [])
        traversal_steps.append(f"Visiting [{current}] (Hop {curr_dist}): inspecting {len(outgoing)} outgoing shipment(s).")

        for edge in outgoing:
            nxt = edge["to"]
            qty = edge["qty"]
            quantities[nxt] = quantities.get(nxt, 0) + qty

            if nxt not in visited:
                visited.add(nxt)
                queue.append(nxt)
                distances[nxt] = curr_dist + 1
                paths[nxt] = paths[current] + [nxt]
                discovered_order.append(nxt)
                traversal_steps.append(f"  -> Discovered reachable facility [{nxt}] via shipment {edge['id']} (qty: {qty:,}).")
            else:
                traversal_steps.append(f"  -> Secondary link to already visited facility [{nxt}] (qty: {qty:,}).")

    latency_ms = round((time.time() - start_time) * 1000, 3)
    traversal_steps.append(f"BFS Complete in {latency_ms} ms. Total reachable downstream facilities: {len(discovered_order)}.")

    return {
        "batchId": batch_id,
        "startOrgId": start_org_id,
        "reachableNodes": discovered_order,
        "hopDistances": distances,
        "paths": paths,
        "quantities": quantities,
        "totalUnitsImpacted": sum(quantities.values()),
        "traversalSteps": traversal_steps,
        "latencyMs": latency_ms
    }

class RequestHandler(SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200)
        self.end_headers()

    def do_GET(self):
        url = urlparse(self.path)
        if url.path == "/api/master-data":
            self.send_json({
                "products": products,
                "batches": batches,
                "organizations": organizations,
                "shipments": shipments,
                "incidents": incidents
            })
        elif url.path == "/api/audit":
            self.send_json(audit_events)
        elif url.path == "/api/recalls":
            self.send_json(list(recalls.values()))
        elif url.path.startswith("/api/recalls/"):
            recall_id = url.path.split("/")[-1]
            rec = recalls.get(recall_id)
            resps = recall_responses.get(recall_id, [])
            if rec:
                self.send_json({"recall": rec, "responses": resps})
            else:
                self.send_error(404, "Recall not found")
        else:
            # Serve static files from current directory
            return super().do_GET()

    def do_POST(self):
        url = urlparse(self.path)
        length = int(self.headers.get('Content-Length', 0))
        post_data = self.rfile.read(length) if length > 0 else b'{}'
        try:
            payload = json.loads(post_data.decode('utf-8'))
        except Exception:
            payload = {}

        if url.path == "/api/trace":
            batch_id = payload.get("batchId", "BATCH-101")
            start_org = payload.get("startOrgId", "M1")
            result = run_bfs(batch_id, start_org)
            log_audit("REGULATORY_ENGINE", "TRACEABILITY", batch_id, "BFS_TRACE",
                      f"Executed downstream BFS from {start_org}. Found {len(result['reachableNodes'])} sites.")
            self.send_json(result)

        elif url.path == "/api/shipments":
            new_id = f"SHP-{len(shipments)+1:03d}"
            shipment = {
                "id": new_id,
                "batchId": payload["batchId"],
                "from": payload["from"],
                "to": payload["to"],
                "qty": int(payload["qty"]),
                "date": payload.get("date", time.strftime("%Y-%m-%d"))
            }
            shipments.append(shipment)
            log_audit("LOGISTICS_OFFICER", "SHIPMENT", new_id, "DISPATCH",
                      f"Shipment {new_id}: {shipment['from']} -> {shipment['to']} ({shipment['qty']:,} units)")
            self.send_json(shipment)

        elif url.path == "/api/recalls/initiate":
            incident_id = payload.get("incidentId", "INC-301")
            batch_id = payload.get("batchId", "BATCH-101")
            reason = payload.get("reason", "Voluntary recall due to reported contamination risk.")
            affected_nodes = payload.get("affectedNodes", [])
            
            recall_id = f"RC-{len(recalls)+2001}"
            recall = {
                "recallId": recall_id,
                "incidentId": incident_id,
                "batchId": batch_id,
                "reason": reason,
                "status": "ACTIVE",
                "createdAt": time.strftime("%Y-%m-%d %H:%M:%S")
            }
            recalls[recall_id] = recall

            resp_list = []
            for org_id in affected_nodes:
                resp_id = f"RESP-{len(resp_list)+5001}"
                resp_list.append({
                    "responseId": resp_id,
                    "recallId": recall_id,
                    "orgId": org_id,
                    "orgName": organizations.get(org_id, {}).get("name", org_id),
                    "orgType": organizations.get(org_id, {}).get("type", "UNKNOWN"),
                    "notifiedAt": time.strftime("%Y-%m-%d %H:%M:%S"),
                    "acknowledgedAt": None,
                    "quarantineStatus": "NOT_QUARANTINED",
                    "completionStatus": "NOTIFIED",
                    "quarantinedQty": 0
                })
            recall_responses[recall_id] = resp_list

            log_audit("REGULATORY_DIRECTOR", "RECALL", recall_id, "INITIATE",
                      f"Initiated recall {recall_id} targeting {len(affected_nodes)} downstream destinations.")
            self.send_json({"recall": recall, "responses": resp_list})

        elif url.path == "/api/recalls/action":
            recall_id = payload.get("recallId")
            org_id = payload.get("orgId")
            action = payload.get("action") # "ACKNOWLEDGE" or "QUARANTINE"
            user = payload.get("userId", f"OFFICER_{org_id}")

            resps = recall_responses.get(recall_id, [])
            target = next((r for r in resps if r["orgId"] == org_id), None)
            if not target:
                self.send_error(404, "Target response directive not found")
                return

            if action == "ACKNOWLEDGE":
                target["acknowledgedAt"] = time.strftime("%Y-%m-%d %H:%M:%S")
                target["completionStatus"] = "ACKNOWLEDGED"
                log_audit(user, "RECALL_RESPONSE", target["responseId"], "ACKNOWLEDGE",
                          f"Facility {org_id} acknowledged recall notification.")
            elif action == "QUARANTINE":
                qty = int(payload.get("quantity", 1000))
                target["quarantineStatus"] = "QUARANTINED"
                target["quarantinedQty"] = qty
                target["completionStatus"] = "ACTIONED"
                log_audit(user, "RECALL_RESPONSE", target["responseId"], "QUARANTINE",
                          f"Facility {org_id} physically quarantined {qty:,} units.")

            self.send_json({"status": "SUCCESS", "response": target})

        elif url.path == "/api/kill-switch":
            batch_id = payload.get("batchId", "BATCH-101")
            lock_status = payload.get("locked", True)
            target_batch = next((b for b in batches if b["batchId"] == batch_id), None)
            if target_batch:
                target_batch["status"] = "QUARANTINED" if lock_status else "ACTIVE"
            
            action_name = "DIGITAL_KILL_SWITCH_ENGAGED" if lock_status else "DIGITAL_KILL_SWITCH_DISENGAGED"
            log_audit("REGULATORY_GATEWAY", "BATCH_LOCK", batch_id, action_name,
                      f"Online e-commerce sales and POS retail checkout status updated to: {'FROZEN' if lock_status else 'UNLOCKED'}")
            self.send_json({"status": "SUCCESS", "batchId": batch_id, "isOnlineBlocked": lock_status})

        elif url.path == "/api/checkout-simulate":
            batch_id = payload.get("batchId", "BATCH-101")
            target_batch = next((b for b in batches if b["batchId"] == batch_id), None)
            is_locked = (target_batch and target_batch.get("status") == "QUARANTINED")
            if is_locked:
                self.send_json({
                    "status": "REJECTED",
                    "httpCode": 403,
                    "message": f"TRANSACTION FORBIDDEN: Batch {batch_id} is under active recall safety lock.",
                    "timestamp": time.strftime("%Y-%m-%d %H:%M:%S")
                })
            else:
                self.send_json({
                    "status": "APPROVED",
                    "httpCode": 200,
                    "message": f"TRANSACTION APPROVED: Batch {batch_id} verified safe for sale.",
                    "timestamp": time.strftime("%Y-%m-%d %H:%M:%S")
                })

        else:
            self.send_error(404, "Endpoint not found")

    def send_json(self, data):
        response = json.dumps(data).encode('utf-8')
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Content-Length', str(len(response)))
        self.end_headers()
        self.wfile.write(response)

def start_server(port=8080):
    server_address = ('', port)
    httpd = HTTPServer(server_address, RequestHandler)
    print(f"=================================================================")
    print(f" BATCHZERO Interactive Web Server running at: http://localhost:{port}")
    print(f" Open http://localhost:{port} in any browser to interact with the app!")
    print(f"=================================================================")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down server.")
        httpd.server_close()

if __name__ == '__main__':
    port = 8080
    if len(sys.argv) > 1:
        port = int(sys.argv[1])
    # Change working directory to directory containing this file
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    start_server(port)
