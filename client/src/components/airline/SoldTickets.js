import React, { useState, useEffect } from "react";
import DateFnsUtils from "@date-io/date-fns";
import dateFormat from "dateformat";
import TextField from "@material-ui/core/TextField";
import { VictoryBar, VictoryChart, VictoryAxis, VictoryLabel } from "victory";
import { makeStyles } from "@material-ui/core/styles";
import { useDispatch, useSelector } from "react-redux";
import Container from "@material-ui/core/Container";
import Button from "@material-ui/core/Button";
import {
  selectAirlineDetails,
  selectSoldTickets
} from "../../store/airline/selectors";
import Grid from "@material-ui/core/Grid";
import {
  KeyboardDatePicker,
  MuiPickersUtilsProvider
} from "@material-ui/pickers";
import { showSoldAirlineTickets } from "../../store/airline/actions";

export default function SoldTickets({ closeModal }) {
  const classes = useStyles();
  const dispatch = useDispatch();

  const [startDate, setStartDate] = useState();
  const [endDate, setEndDate] = useState();

  const airline = useSelector(selectAirlineDetails);
  const soldTickets = useSelector(selectSoldTickets);
  function handleShow() {
    dispatch(
      showSoldAirlineTickets({
        id: airline.id,
        startDate: startDate,
        endDate: endDate
      })
    );
  }

  return (
    <Container
      classes={{
        root: classes.serviceRow
      }}
    >
      <Grid container spacing={3}>
        <Grid item xs={3}>
          <TextField
            required
            label="Name"
            defaultValue={airline.name}
            className={classes.textField}
            margin="normal"
            type="text"
            readOnly={true}
          />
        </Grid>
        <Grid item xs={4}>
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              disableToolbar
              variant="inline"
              format="dd/MM/yyyy"
              margin="normal"
              label="Start date"
              value={startDate}
              onChange={date => setStartDate(dateFormat(date, "yyyy-mm-dd"))}
              KeyboardButtonProps={{
                "aria-label": "change date"
              }}
            />
          </MuiPickersUtilsProvider>
        </Grid>
        <Grid item xs={4}>
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              disableToolbar
              minDate={!!startDate ? startDate : new Date()}
              variant="inline"
              format="dd/MM/yyyy"
              margin="normal"
              label="End date"
              value={endDate}
              onChange={date => setEndDate(dateFormat(date, "yyyy-mm-dd"))}
              KeyboardButtonProps={{
                "aria-label": "change date"
              }}
            />
          </MuiPickersUtilsProvider>
        </Grid>
        <Grid item xs={1}>
          <Button
            variant="contained"
            color="primary"
            className={classes.button}
            onClick={handleShow}
          >
            Show
          </Button>
        </Grid>
      </Grid>
      <VictoryChart
        // domainPadding will add space to each side of VictoryBar to
        // prevent it from overlapping the axis
        domainPadding={120}
      >
        <VictoryAxis
          // tickValues specifies both the number of ticks and where
          // they are placed on the axis
          tickValues={[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]}
        />
        <VictoryAxis
          dependentAxis
          // tickFormat specifies how ticks should be displayed
          tickFormat={x => `${x}`}
        />
        <VictoryBar
          data={soldTickets}
          x="ticket"
          y="soldTickets"
          style={{ data: { fill: "#008080" } }}
        />
      </VictoryChart>
    </Container>
  );
}

const useStyles = makeStyles(theme => ({
  root: {
    position: "absolute",
    left: "20%",
    top: "5%",
    width: "60%",
    display: "flex",
    flexDirection: "column"
  },
  serviceRow: {
    display: "flex",
    flexDirection: "column",
    width: "100%"
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    flex: 1,
    width: 150
  },
  margin: {
    margin: theme.spacing(1)
  },
  button: {
    margin: theme.spacing(1),
    width: "30%",
    marginLeft: "auto",
    background: "#008080"
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120
  },
  selectEmpty: {
    marginTop: theme.spacing(2)
  },
  listScroll: {
    maxHeight: "370px",
    overflow: "scroll"
  }
}));
