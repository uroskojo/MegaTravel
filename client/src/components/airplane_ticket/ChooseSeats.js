import React, { useState, useEffect } from "react";
import { makeStyles } from "@material-ui/core/styles";
import { useDispatch, useSelector } from "react-redux";
import TextField from "@material-ui/core/TextField";
import Container from "@material-ui/core/Container";
import Button from "@material-ui/core/Button";
import { makeTicketReservation } from "../../store/airplane_ticket/actions";
import { selectSeats } from "../../store/airplane_ticket/selectors";
import {
  putSelectedSeats,
  fetchFlight,
  clearSelectedSeats
} from "../../store/airplane_ticket/actions";
import { Link } from "react-router-dom";
import { REQUEST_TYPE } from "../../constants/user";
import { selectSelectedFlight } from "../../store/airplane_ticket/selectors";

export default function ChooseSeats({ match, history }) {
  const classes = useStyles();
  const dispatch = useDispatch();
  const selectedSeats = useSelector(selectSeats);
  const selectedFlight = useSelector(selectSelectedFlight);
  const [choosenSeatCoordinates, setChoosenSeatCoordinates] = useState({
    segmentNumber: 0,
    rowNumber: 0,
    columnNumber: 0
  });

  const [ticket, setTicket] = useState({
    seats: [],
    flightID: match.params.flight_id,
    invitedUsers: null
  });

  useEffect(() => {
    dispatch(fetchFlight(match.params.flight_id));
  }, [match.params.flight_id]);

  function handleChooseButton() {
    dispatch(putSelectedSeats(choosenSeatCoordinates));
  }

  function handleChangeSeatCoordinates(event) {
    event.persist();

    setChoosenSeatCoordinates(oldValues => ({
      ...oldValues,
      [event.target.name]: Number(event.target.value)
    }));
  }

  function handleReserveButton() {
    ticket.seats = selectedSeats;
    dispatch(
      makeTicketReservation({
        ticket,
        callback: ticketId => {
          history.push({
            pathname: `/hotel-reservation`,
            state: { airplaneTicketId: ticketId }
          });
        }
      })
    );
    dispatch(clearSelectedSeats([]));
  }

  return (
    <Container
      classes={{
        root: classes.root
      }}
    >
      <h2>Choose one or more seat ({selectedSeats.length})</h2>

      <TextField
        label="Segment number "
        margin="normal"
        type="number"
        className={classes.textField}
        onChange={handleChangeSeatCoordinates}
        inputProps={{
          min: "1",
          max: String(selectedFlight.airplane.numberOfSegments),
          name: "segmentNumber",
          id: "segment-num"
        }}
      />

      <TextField
        label="Column number"
        margin="normal"
        type="number"
        className={classes.textField}
        onChange={handleChangeSeatCoordinates}
        inputProps={{
          min: "1",
          max: String(selectedFlight.airplane.numberOfColumnsPerSegment),
          name: "columnNumber",
          id: "column-num"
        }}
      />

      <TextField
        label="Row number"
        margin="normal"
        type="number"
        className={classes.textField}
        onChange={handleChangeSeatCoordinates}
        inputProps={{
          min: "1",
          max: String(selectedFlight.airplane.numberOfRows),
          name: "rowNumber",
          id: "row-num"
        }}
      />

      <Button
        variant="contained"
        color="primary"
        className={classes.button}
        onClick={handleChooseButton}
      >
        Choose
      </Button>
      {selectedSeats.length === 1 && (
        <Button
          variant="contained"
          color="primary"
          className={classes.button}
          onClick={handleReserveButton}
        >
          Reserve
        </Button>
      )}
      {selectedSeats.length > 1 && (
        <Link
          to={
            "/ticket-reservation/" +
            match.params.flight_id +
            "/choose-seat" +
            "/search/" +
            REQUEST_TYPE.GROUP_TRIP
          }
        >
          <Button
            variant="contained"
            color="primary"
            className={classes.button}
          >
            Next
          </Button>
        </Link>
      )}
    </Container>
  );
}
const useStyles = makeStyles(theme => ({
  root: {
    display: "flex",
    flexDirection: "column",
    width: "100%"
  },
  inputs: {
    display: "flex",
    flexDirection: "column"
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    width: "25%"
  },
  inputContainer: {
    padding: "0px 0px 0px 0px"
  },
  modalContainer: {
    width: "60%",
    height: "80%",
    display: "flex",
    flexDirection: "column",
    background: "#cce8ff",
    padding: "0px 0px 0px 0px",
    border: "0px none",
    justifyContent: "flex-end"
  },
  button: {
    margin: theme.spacing(1),
    width: "30%"
  }
}));
