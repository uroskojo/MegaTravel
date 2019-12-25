import {
  PUT_AIRLINE_DETAILS,
  PUT_AIRLINE_LOCATION_INFORMATION,
  SAVE_AIRLINE_DETAILS,
  FETCH_AIRLINE_DETAILS,
  FETCH_AIRLINE_DESTINATIONS,
  PUT_AIRLINE_DESTINATIONS,
  PUT_AIRLINE_FLIGHTS,
  FETCH_AIRLINE_FLIGHTS,
  FETCH_TICKETS_FOR_FAST_RESERVATION,
  PUT_TICKETS_FOR_FAST_RESERVATION,
  CREATE_FAST_TICKET_RESERVATION,
  PUT_AIRPLANE_DETAILS,
  SAVE_AIRPLANE_DETAILS,
  FETCH_AIRLINE_AIRPLANES,
  PUT_AIRLINE_AIRPLANES,
  SAVE_FLIGHT,
  FETCH_AIRLINE_RATING,
  PUT_AIRLINE_RATING,
  RATE_AIRLINE,
  RATE_FLIGHT,
  CANCEL_FLIGHT,
  SHOW_AIRLINE_INCOME,
  PUT_AIRLINE_INCOME,
  FETCH_AIRLINE_ADMIN,
  PUT_AIRLINE_ADMIN,
  SHOW_SOLD_AIRLINE_TICKETS,
  PUT_SOLD_AIRLINE_TICKETS
} from "./constants";

export const putSoldTicketsData = payload => ({
  type: PUT_SOLD_AIRLINE_TICKETS,
  payload
});

export const showSoldAirlineTickets = payload => ({
  type: SHOW_SOLD_AIRLINE_TICKETS,
  payload
});

export const fetchAirlineAdmin = payload => ({
  type: FETCH_AIRLINE_ADMIN,
  payload
});

export const putIncomeData = payload => ({
  type: PUT_AIRLINE_INCOME,
  payload
});

export const putAirlineDetails = payload => ({
  type: PUT_AIRLINE_DETAILS,
  payload
});

export const putAirlineLocationInformation = payload => ({
  type: PUT_AIRLINE_LOCATION_INFORMATION,
  payload
});

export const saveAirlineDetails = payload => ({
  type: SAVE_AIRLINE_DETAILS,
  payload
});

export const fetchAirlineDetails = payload => ({
  type: FETCH_AIRLINE_DETAILS,
  payload
});

export const fetchAirlineDestinations = payload => ({
  type: FETCH_AIRLINE_DESTINATIONS,
  payload
});

export const putAirlineDestinations = payload => ({
  type: PUT_AIRLINE_DESTINATIONS,
  payload
});

export const putAirlineFlights = payload => ({
  type: PUT_AIRLINE_FLIGHTS,
  payload
});

export const fetchAirlineFlights = payload => ({
  type: FETCH_AIRLINE_FLIGHTS,
  payload
});

export const fetchTicketsForFastReservation = payload => ({
  type: FETCH_TICKETS_FOR_FAST_RESERVATION,
  payload
});

export const putTicketsForFastReservation = payload => ({
  type: PUT_TICKETS_FOR_FAST_RESERVATION,
  payload
});

export const createFastTicketReservation = payload => ({
  type: CREATE_FAST_TICKET_RESERVATION,
  payload
});

export const putAirplaneDetails = payload => ({
  type: PUT_AIRPLANE_DETAILS,
  payload
});

export const saveAirplaneDetails = payload => ({
  type: SAVE_AIRPLANE_DETAILS,
  payload
});

export const fetchAirlineAirplanes = payload => ({
  type: FETCH_AIRLINE_AIRPLANES,
  payload
});

export const putAirlineAirplanes = payload => ({
  type: PUT_AIRLINE_AIRPLANES,
  payload
});

export const saveFlight = payload => ({
  type: SAVE_FLIGHT,
  payload
});

export const fetchAirlineRating = payload => ({
  type: FETCH_AIRLINE_RATING,
  payload
});

export const putAirlineRating = payload => ({
  type: PUT_AIRLINE_RATING,
  payload
});

export const rateAirline = payload => ({
  type: RATE_AIRLINE,
  payload
});

export const rateFlight = payload => ({
  type: RATE_FLIGHT,
  payload
});

export const cancelFlight = payload => ({
  type: CANCEL_FLIGHT,
  payload
});

export const showAirlineIncome = payload => ({
  type: SHOW_AIRLINE_INCOME,
  payload
});

export const putAirlineAdmin = payload => ({
  type: PUT_AIRLINE_ADMIN,
  payload
});
