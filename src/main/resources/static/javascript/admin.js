 // Load events dynamically
      async function loadEvents() {
        const res = await fetch("http://localhost:8080/api/event/all");
        const data = await res.json();
        const tableBody = document.getElementById("eventTableBody");
        tableBody.innerHTML = "";

        data.forEach((event) => {
          const row = document.createElement("tr");
          row.innerHTML = `
            <td>${event.id}</td>
            <td>${event.title}</td>
            <td>${event.venue}</td>
            <td>${event.date}</td>
            <td>${event.category}</td>
            <td>
              <button class="btn btn-sm btn-warning" onclick='openEditModal(${JSON.stringify(
                event
              )})'>Edit</button>
            </td>
          `;
          tableBody.appendChild(row);
        });
      }

      loadEvents(); // Load on page open

      // Add Event Form Submission
      document
        .getElementById("addEventForm")
        .addEventListener("submit", function (e) {
          e.preventDefault(); // prevent page reload

          const eventData = {
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            venue: document.getElementById("venue").value,
            speakers: document.getElementById("speakers").value,
            date: document.getElementById("date").value,
            category: document.getElementById("category").value,
            adminId: document.getElementById("adminId").value,
          };

          fetch("http://localhost:8080/api/event/add", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(eventData),
          })
            .then((response) => {
              if (!response.ok) throw new Error("Network response was not ok");
              return response.json();
            })
            .then((data) => {
              alert("✅ Event added successfully!");
              location.reload(); // reload to show the new event in the table
            })
            .catch((error) => {
              console.error("Error:", error);
              alert("❌ Failed to add event. Please try again.");
            });
        });

      // Delete Event Function
      function deleteEvent(eventId) {
        if (!confirm("Are you sure you want to delete this event?")) {
          return;
        }

        fetch(`http://localhost:8080/api/event/delete/${eventId}`, {
          method: "DELETE",
          redirect: "follow",
        })
          .then((response) => {
            if (response.ok) {
              alert("✅ Event deleted successfully!");
              location.reload(); // refresh to update table
            } else {
              alert("❌ Failed to delete event. Please try again.");
            }
            return response.text();
          })
          .then((result) => console.log("Server response:", result))
          .catch((error) => {
            console.error("Error:", error);
            alert("⚠️ Something went wrong while deleting the event.");
          });
      }

      

      // Function to open edit modal with data
      function openEditModal(button) {
        document.getElementById("editEventId").value =
          button.getAttribute("data-id");
        document.getElementById("editTitle").value =
          button.getAttribute("data-title") || "";
        document.getElementById("editVenue").value =
          button.getAttribute("data-venue") || "";
        document.getElementById("editSpeakers").value =
          button.getAttribute("data-speakers") || "";
        document.getElementById("editDate").value =
          button.getAttribute("data-date") || "";
        document.getElementById("editCategory").value =
          button.getAttribute("data-category") || "";
        document.getElementById("editDescription").value =
          button.getAttribute("data-description") || "";

        new bootstrap.Modal(document.getElementById("editEventModal")).show();
      }

      // Submit edited event
      document
        .getElementById("editEventForm")
        .addEventListener("submit", function (e) {
          e.preventDefault();

          const eventId = document.getElementById("editEventId").value;

          const updatedEvent = {
            title: document.getElementById("editTitle").value,
            description: document.getElementById("editDescription").value,
            venue: document.getElementById("editVenue").value,
            speakers: document.getElementById("editSpeakers").value,
            date: document.getElementById("editDate").value,
            category: document.getElementById("editCategory").value,
            adminId: document.getElementById("editAdminId").value,
          };

          fetch(`http://localhost:8080/api/event/edit/${eventId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedEvent),
          })
            .then((response) => {
              if (!response.ok) throw new Error("Failed to update event");
              return response.json();
            })
            .then((data) => {
              alert("✅ Event updated successfully!");
              location.reload();
            })
            .catch((error) => {
              console.error("Error:", error);
              alert(
                "❌ Failed to update event. Check the console for details."
              );
            });
        });




// Search Functionality (Dynamic Frontend Search)
document.getElementById("searchForm").addEventListener("submit", async (e) => {
    e.preventDefault(); // prevent page reload

    const speakers = e.target.speakers.value;
    const venue = e.target.venue.value;
    const category = e.target.category.value;

    const params = new URLSearchParams();
    if (speakers) params.append("speakers", speakers);
    if (venue) params.append("venue", venue);
    if (category) params.append("category", category);

    try {
      const res = await fetch(`http://localhost:8080/api/event/search?${params.toString()}`);
      const events = await res.json();

      console.log(events); 

      const container = document.querySelector(".event-cards");
      container.innerHTML = ""; // clear old results
      events.forEach(event => {
        container.innerHTML += `
          <div class="card">
            <h5>${event.title}</h5>
            <p>${event.category} | ${event.venue} | ${event.speakers}</p>
          </div>`;
      });
    } catch (err) {
      console.error("Search failed:", err);
    }
  });

// Function to fetch all participants
function loadParticipants() {
  fetch('/api/participants') // create a REST endpoint that returns all participants
    .then(response => response.json())
    .then(data => {
      const tbody = document.getElementById('participantsTableBody');
      tbody.innerHTML = ""; // clear old rows

      if (data.length === 0) {
        tbody.innerHTML = "<tr><td colspan='5' class='text-muted'>No participants registered yet</td></tr>";
        return;
      }

      data.forEach(participant => {
        const row = document.createElement('tr');

        row.innerHTML = `
          <td>${participant.id}</td>
          <td>${participant.fullName}</td>
          <td>${participant.email}</td>
          <td>${participant.event.title}</td>
          <td>${new Date(participant.registeredDate).toLocaleString()}</td>
        `;
        tbody.appendChild(row);
      });
    })
    .catch(error => console.error("Error fetching participants:", error));
}

// Initial load
loadParticipants();

// Refresh participants every 5 seconds
setInterval(loadParticipants, 5000);


async function loadEvents() {
  try {
    const response = await fetch("/api/event/all");
    const events = await response.json();
    document.getElementById("eventsContainer").innerHTML = events.map(e => `<p>${e.title}</p>`).join("");
  } catch (err) {
    console.error("Error loading events:", err);
  }
}

async function loadParticipants() {
  try {
    const response = await fetch("/api/participants");
    const participants = await response.json();
    document.getElementById("participantsContainer").innerHTML = participants.map(p => `<p>${p.name}</p>`).join("");
  } catch (err) {
    console.error("Error fetching participants:", err);
  }
}

loadEvents();
loadParticipants();

// View Attendees Modal
function viewAttendees(eventId) {
  const modal = new bootstrap.Modal(document.getElementById("attendeesModal"));
  const tableBody = document.getElementById("attendeesTableBody");
  const eventTitle = document.getElementById("attendeesEventTitle");

  // Optional: Show the event title (if available in row)
  const eventRow = document.querySelector(`[onclick='viewAttendees(${eventId})']`).closest("tr");
  const title = eventRow.querySelector("td:nth-child(2)").textContent;
  eventTitle.textContent = title;

  // Clear old rows
  tableBody.innerHTML = `<tr><td colspan="4" class="text-muted">Loading...</td></tr>`;

  // Fetch attendees via AJAX
  fetch(`/admin/event/${eventId}/attendees`)
    .then((response) => response.json())
    .then((data) => {
      if (data.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="4" class="text-muted">No attendees found for this event.</td></tr>`;
      } else {
        tableBody.innerHTML = data
          .map(
            (attendee) => `
              <tr>
                <td>${attendee.id}</td>
                <td>${attendee.participantName}</td>
                <td>${attendee.participantEmail}</td>
                <td>${new Date(attendee.registeredDate).toLocaleString()}</td>
              </tr>
            `
          )
          .join("");
      }
    })
    .catch((error) => {
      console.error("Error fetching attendees:", error);
      tableBody.innerHTML = `<tr><td colspan="4" class="text-danger">Error loading attendees</td></tr>`;
    });

  modal.show();
}
