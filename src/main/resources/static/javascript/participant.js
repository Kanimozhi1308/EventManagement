    // --- Modal preparation for Cancellation ---
      function prepareCancelModal(button) {
        const registrationId = button.getAttribute("data-reg-id");
        const eventTitle = button.getAttribute("data-event-title");

        // Set the event title in the modal body
        document.getElementById("eventTitlePlaceholder").textContent =
          eventTitle;
        // Set the cancel URL for the confirmation button
        const cancelUrl = "/participant/cancel/" + registrationId;
        document
          .getElementById("confirmCancelButton")
          .setAttribute("href", cancelUrl);
      }

      // --- Client-Side Event Filtering ---
      function filterEvents() {
        const titleFilterText = document
          .getElementById("titleFilter")
          .value.toLowerCase();
        const categoryFilterText = document
          .getElementById("categoryFilter")
          .value.toLowerCase();
        const events = document.querySelectorAll("#eventsGrid .col");

        events.forEach((col) => {
          const card = col.querySelector(".event-card");

          // Get data attributes set by Thymeleaf
          const eventTitle = card.getAttribute("data-title");
          const eventCategory = card.getAttribute("data-category");
          const eventVenue = card.getAttribute("data-venue");

          let matchesTitleOrVenue = false;
          if (
            titleFilterText.length === 0 ||
            eventTitle.includes(titleFilterText) ||
            eventVenue.includes(titleFilterText)
          ) {
            matchesTitleOrVenue = true;
          }

          let matchesCategory = false;
          if (
            categoryFilterText.length === 0 ||
            eventCategory.includes(categoryFilterText)
          ) {
            matchesCategory = true;
          }

          // Show the card only if both filters match
          if (matchesTitleOrVenue && matchesCategory) {
            col.style.display = ""; // Show
          } else {
            col.style.display = "none"; // Hide
          }
        });
      }

  // Load Participant Registrations
  document.addEventListener("DOMContentLoaded", async () => {
    const userId = /*[[${userId}]]*/ 0; // Thymeleaf injects userId

    try {
      const response = await fetch(`/api/participant/my-registrations/${userId}`);
      const registrations = await response.json();

      const grid = document.getElementById("eventsGrid");
      const noEvents = document.getElementById("noEventsAlert");

      if (!registrations || registrations.length === 0) {
        noEvents.classList.remove("d-none");
        return;
      }

      registrations.forEach((reg) => {
        const event = reg.event;
        const isUpcoming =
          new Date(event.date) >= new Date().setHours(0, 0, 0, 0);

        const card = document.createElement("div");
        card.className = "col";

        card.innerHTML = `
          <div class="card h-100 event-card ${
            isUpcoming ? "status-upcoming" : "status-past"
          }">
            <div class="card-body d-flex flex-column">
              <div class="card-header-status mb-3 border-0 p-0">
                <span class="registration-badge badge ${
                  isUpcoming ? "bg-primary" : "bg-secondary"
                }">
                  ${isUpcoming ? "Upcoming" : "Past Event"}
                </span>
              </div>

              <h4 class="card-title text-truncate">${event.title}</h4>

              <p class="card-text mb-1">
                <i class="bi bi-calendar-event me-2 text-muted"></i>
                <strong>${new Date(event.date).toLocaleDateString()}</strong>
              </p>

              <p class="card-text mb-3">
                <i class="bi bi-geo-alt-fill me-2 text-muted"></i>
                ${event.venue}
              </p>

              <p class="card-text mb-3 small text-muted">
                Registered On: ${new Date(reg.registeredDate).toLocaleString()}
              </p>

              <div class="mt-auto pt-3 border-top">
                <a href="/participant/view/${reg.id}"
                  class="btn btn-sm btn-outline-dark w-100 mb-2">
                  <i class="bi bi-file-earmark-text"></i> View Details
                </a>

                ${
                  isUpcoming
                    ? `<button
                      class="btn btn-sm btn-outline-danger w-100"
                      onclick="cancelRegistration(${reg.id}, '${event.title}')">
                      <i class="bi bi-x-circle"></i> Cancel Registration
                    </button>`
                    : ""
                }
              </div>
            </div>
          </div>
        `;

        grid.appendChild(card);
      });
    } catch (error) {
      console.error("Error fetching registrations:", error);
    }
  });

  // Cancel Registration (calls backend endpoint)
  function cancelRegistration(id, title) {
    if (confirm(`Are you sure you want to cancel registration for "${title}"?`)) {
      window.location.href = `/participant/cancel/${id}`;
    }
  }



     // Modal handler for cancel button
      const cancelModal = document.getElementById("cancelRegistrationModal");
      cancelModal.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget;
        const eventTitle = button.getAttribute("data-eventtitle");
        const cancelUrl = button.getAttribute("data-cancelurl");

        document.getElementById("eventTitlePlaceholder").textContent =
          eventTitle;
        document.getElementById("confirmCancelButton").href = cancelUrl;
      });

      // Simple filter function for the dashboard
      function filterEvents() {
        const titleInput = document
          .getElementById("titleFilter")
          .value.toLowerCase();
        const categoryInput = document
          .getElementById("categoryFilter")
          .value.toLowerCase();
        const cards = document.querySelectorAll("#eventsGrid .col");

        cards.forEach((card) => {
          const title = card
            .getAttribute("data-event-title")
            .toLowerCase();
          const category = card.getAttribute("data-category").toLowerCase();
          if (
            title.includes(titleInput) &&
            category.includes(categoryInput)
          ) {
            card.style.display = "";
          } else {
            card.style.display = "none";
          }
        });
      }

// Load registered events
async function loadRegisteredEvents() {
  const response = await fetch(`/api/participants/user/${userId}/events`);
  const container = document.getElementById("registeredEventsContainer");

  if (!response.ok) {
    container.innerHTML = "<p>Error loading registered events</p>";
    return;
  }

  const registrations = await response.json();
  container.innerHTML = registrations.map(reg => `
    <div class="col-md-4">
      <div class="card p-3 shadow-sm bg-light">
        <h5>${reg.eventTitle}</h5>
        <p>Date: ${reg.eventDate}</p>
        <p>Venue: ${reg.eventVenue}</p>
        <p>Registration Status: ${reg.registration.status || 'Confirmed'}</p>
      </div>
    </div>
  `).join("");
}


  // Modal handler for register button


  const registerModal = document.getElementById("registerModal");
  registerModal.addEventListener("show.bs.modal", function (event) {
    const button = event.relatedTarget;
    const eventId = button.getAttribute("data-eventid");
    const title = button.getAttribute("data-title");

    document.getElementById("modalEventId").value = eventId;
    document.getElementById("modalEventTitle").value = title;
  });


    document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("searchForm");
    const resetBtn = document.createElement("button");
    resetBtn.type = "button";
    resetBtn.className = "btn btn-secondary ms-2";
    resetBtn.textContent = "Reset";
    resetBtn.onclick = () => {
      window.location.href = "/participant/dashboard";
    };
    form.appendChild(resetBtn);
  });
