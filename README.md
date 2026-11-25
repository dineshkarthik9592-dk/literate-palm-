# BATCHZERO — Enterprise Product Traceability & Recall Management System

![Java 17](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![SRM University-AP](https://img.shields.io/badge/SRM%20University--AP-CSE%20213-blue?style=for-the-badge)

**Course:** CSE 213: Advanced Java Programming  
**Faculty Evaluator:** Dr. Anwesha Kashyap, Assistant Professor, Dept. of CSE  
**Institution:** School of Engineering and Sciences, SRM University–AP  
**Academic Year:** 2026–2027 [Odd Semester]

---

## 👥 Project Team & Technical Ownership

| Member Name | Registration No. | Core Technical Contribution |
| :--- | :--- | :--- |
| **PRUDHVI RAJ** | `AP25110070169` | Problem Formulation & Java Domain Invariants (`Product`, `Batch`, `Organization`) |
| **ISHANTH CH** | `AP25110070116` | Graph Modeling & BFS Downstream Traversal Engine ($O(V + E)$) |
| **E SAI VIKAS** | `AP25110070655` | 5-Tier System Architecture, MySQL 3NF Schema & ACID Transaction Management |
| **P. BHANU SRIRAM** | `AP25110070620` | Recall State Machine, Interactive Web Dashboard & Emergency Digital Kill-Switch |
| **M DINESH KARTHIK** | `AP25110070508` | Automated Verification Test Suites, 2.25 ms Benchmarks & FDA 21 CFR Part 11 Audit Trail |

---

## 🏛️ 5-Tier System Architecture

```text
[ Tier 1: Presentation & Visualization ]  (Semantic HTML5 • Native SVG Vector Canvas • Matte Ash-Black UI)
                   │
                   ▼  (JSON over HTTP/1.1 REST)
[ Tier 2: API Gateway & Controller ]      (Stateless Endpoints: /api/trace, /api/kill-switch, /api/checkout-simulate)
                   │
                   ▼  (Service Invocations)
[ Tier 3: Business Logic & Workflows ]    (Recall Coordination • Directives State Machine • Audit Event Logging)
                   │
                   ▼  (In-Memory Graph Queries)
[ Tier 4: Domain & Graph Engine ]         (Java 17 OOP Invariants • Adjacency List • BFS Queue with Cycle Protection)
                   │
                   ▼  (JDBC PreparedStatement & Transaction Demarcation)
[ Tier 5: Persistence & ACID Storage ]   (MySQL 8.0 • 8 Normalized 3NF Tables • Commit / Rollback Boundaries)
```

---

## ⚡ Algorithmic Core: Downstream BFS Traversal

* **Graph Representation:** Directed Multigraph $G = (V, E)$, where $V$ represents supply chain organizations (Factories, Warehouses, Distributors, Retailers) and $E$ represents verified batch shipments.
* **Traversal Strategy:** Level-by-level **Breadth-First Search (BFS)** computes minimum hop distance $d(s, v)$ from the contamination origin $s$, prioritizing primary logistics hubs over secondary retail shops.
* **Cycle Prevention:** In-memory `HashSet<String> visited` eliminates infinite loops in $O(1)$ time in cyclic re-shipment scenarios.
* **Complexity:** Time complexity $O(V + E)$, Space complexity $O(V)$.
* **Empirical Latency:** **2.25 ms** across an 8-facility multi-tier network (tested on OpenJDK).

---

## 🔒 Emergency Digital Kill-Switch (Online Sales Lock)

To eliminate the fatal gap between recall notification and retail execution, BATCHZERO provides an **API-driven sales kill-switch**:
1. When a quality incident occurs, the regulatory officer triggers **"Freeze All Online & POS Sales"**.
2. BATCHZERO pushes a digital blacklist directive that marks the batch barcode as `QUARANTINED`.
3. Connected e-commerce checkout APIs (Apollo 24/7, Tata 1mg, Netmeds) and in-store cash register scanners query `POST /api/checkout-simulate`.
4. The transaction is instantly aborted with **`HTTP 403 FORBIDDEN`**, preventing tainted drugs from reaching consumers.

---

## 🗄️ Database Schema & ACID Compliance

Normalized into 8 tables in **Third Normal Form (3NF)**:
1. `products` — Master catalog definitions.
2. `batches` — Production lots, expiration timelines, and quarantine statuses.
3. `organizations` — Supply chain actors and facility types.
4. `shipments` — Directed movements forming graph edges.
5. `incidents` — Quality alerts and inspection reports.
6. `recalls` — Formal recall campaign records.
7. `recall_responses` — Destination directive states (`NOTIFIED` $\to$ `ACKNOWLEDGED` $\to$ `QUARANTINED`).
8. `audit_events` — Immutable, append-only audit trail compliant with **US FDA 21 CFR Part 11**.

### ACID Demarcation (`RecallDAO.java`):
```java
conn.setAutoCommit(false); // Begin transaction
try {
    stmtRecall.executeUpdate();
    for (RecallResponse r : responses) {
        stmtResp.addBatch();
    }
    stmtResp.executeBatch();
    conn.commit(); // Atomic success across all facilities
} catch (SQLException ex) {
    conn.rollback(); // Complete rollback prevents corrupted half-written records
    throw ex;
} finally {
    conn.setAutoCommit(originalAutoCommit);
}
```

---

## 🚀 How to Run

### 1. Run the Java Backend Engine:
```bash
# Compile and run test harness
javac -d bin src/main/java/com/batchzero/**/*.java
java -cp bin com.batchzero.BatchZeroApplication
```

### 2. Launch the Interactive Web Dashboard & REST Server:
```bash
python web/app.py 8080
# Open http://localhost:8080/index.html in any browser
```

### 3. Standalone Offline Demo:
Double-click `index.html` to run 100% offline with zero dependencies!
