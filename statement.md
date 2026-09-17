# Project Statement & Problem Scope

## 1. Problem Statement
Managing events, ticketing, and seat reservations in academic institutions, entertainment venues, and corporate events often relies on manual processes or fragmented spreadsheets. This approach suffers from critical limitations:
- **Double-booking errors**: Multiple users accidentally booking the exact same seat due to a lack of synchronized seat locking.
- **Opaque seat pricing & layouts**: Inability for attendees to view seat maps with category-based pricing (VIP, Regular, Economy).
- **Manual tracking & reporting**: Inefficient manual calculation of revenue, ticket cancellations, and venue occupancy rates.
- **System bloat**: Unnecessary complexity in web/desktop GUI frameworks when a lightweight, robust command-line tool is required.

The **Event Ticket & Seat Management System** provides an efficient, terminal-based Java application that automates user authentication, dynamic seat layout generation, ticket reservation, cancellation refunds, and executive reporting.

---

## 2. Scope of the Project
The project scope includes:
- **User Authentication & RBAC**: Account creation, login, and permission scoping for Administrators and Customers.
- **Dynamic Event & Seat Grid Management**: Admin creation of events with customizable seating grids and automated tier pricing.
- **Interactive Seat Reservation**: Real-time visualization of seat availability, atomic booking transactions, and double-booking prevention.
- **Ticket Cancellation & Refunds**: Cancellation of active bookings with automatic seat release and refund calculations.
- **Executive Analytics & E-Ticket Generation**: Revenue calculations, venue occupancy metrics, and digital e-ticket exports to text files.
- **Persistent Data Storage**: Local serialization maintaining system state across CLI restarts without external database server overhead.
- **Automated Testing & Verification**: Self-contained unit test suite verifying core system modules.

### Out of Scope
- Online credit card payment gateway processing (simulated payment confirmation).
- Graphical User Interface (GUI) / Web browser interface (strictly Terminal CLI based).

---

## 3. Target Users
1. **Event Administrators & Organizers**:
   - Create and schedule new events.
   - Configure venue seating dimensions and base ticket pricing.
   - Monitor real-time seat occupancy and total system revenue.
   - Manage or cancel any customer booking when necessary.

2. **Event Attendees / Customers**:
   - Register account and log in securely.
   - Browse upcoming events and search events by keyword or category.
   - Inspect visual seat maps and select desired seats.
   - View booking history and export printable e-tickets (.txt).
   - Cancel tickets if plans change.

---

## 4. High-Level Features
- **Multi-Role User Access Control**: Customer and Administrator portals.
- **Interactive ASCII Seat Maps**: Visual seat grid display (`[V A1]`, `[X BOOK]`).
- **Tiered Pricing Engine**: Automatic markups/discounts (VIP 150%, Regular 100%, Economy 85%).
- **Atomic Seat Locking**: Prevents double-booking reserved seats.
- **File Persistence**: Local Object Serialization (`data/system_store.dat`).
- **E-Ticket Exporter**: Printable ticket receipts (`tickets/Ticket_<ID>.txt`).
- **Executive Analytics**: Gross revenue and venue occupancy reports.
- **Automated Unit Test Suite**: 28 automated assertions covering all core modules.
