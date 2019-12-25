import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { makeStyles } from '@material-ui/core/styles';
import { Container } from '@material-ui/core';
import Box from '@material-ui/core/Box';
import Icon from '@material-ui/core/Icon';
import Modal from '@material-ui/core/Modal';
import { Grid } from '@material-ui/core';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import {
  KeyboardDatePicker,
  MuiPickersUtilsProvider
} from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
import dateFormat from 'dateformat';
import { selectRentACarVehicles } from '../../store/rent-a-car/selectors';
import { userDataSelector } from '../../store/user/selectors';
import RentACarVehicle from './Vehicle';
import {
  fetchRentACarVehicles,
  fetchRentACarVehiclesOnDiscount,
  searchVehicles,
  sortVehicles
} from '../../store/rent-a-car/actions';
import CreateVehicle from './CreateVehicle';
import Background from '../../assets/background.jpg';

export default function RentACarVehicles({ rentACarId, airplaneTicket }) {
  const user = useSelector(userDataSelector);
  const vehicles = useSelector(selectRentACarVehicles);
  const classes = useStyles();
  const dispatch = useDispatch();
  const [createModalVisibility, setCreateModalVisibility] = useState(false);
  const [modalStyle] = React.useState(getModalStyle);

  const [pickUpDate, setPickUpDate] = useState();
  const [dropOffDate, setDropOffDate] = useState();
  const [pickUpLocation, setPickUpLocation] = useState('');
  const [dropOffLocation, setDropOffLocation] = useState('');
  const [type, setType] = useState('');
  const [seats, setSeats] = useState(0);
  const [price, setPrice] = useState({
    min: 0,
    max: 0
  });

  function closeModal() {
    setCreateModalVisibility(false);
  }

  useEffect(() => {
    // if (location != undefined) {
    //   setPickUpDate(location.state.pickUpDate);
    //   setDropOffDate(location.state.dropOffDate);
    // }

    dispatch(
      fetchRentACarVehicles({
        rentACarId
      })
    );

    //TODO replace with airplane ticket start/end date
    dispatch(
      fetchRentACarVehiclesOnDiscount({
        rentACarId: rentACarId,
        pickUpDate: dateFormat(new Date(), 'yyyy-mm-dd'),
        dropOffDate: dateFormat(new Date(), 'yyyy-mm-dd')
      })
    );
  }, []);

  function handleSearch() {
    dispatch(
      searchVehicles({
        pickUpDate,
        dropOffDate,
        pickUpLocation,
        pickUpDate,
        dropOffLocation,
        type,
        seats,
        startRange: price.min,
        endRange: price.max,
        rentACarId
      })
    );
  }

  function handleSortByBrand() {
    dispatch(sortVehicles({ by: 'brand', rentACarId: rentACarId }));
  }

  function handleSortByModel() {
    dispatch(sortVehicles({ by: 'model', rentACarId: rentACarId }));
  }

  function handleSortByYop() {
    dispatch(sortVehicles({ by: 'yearOfProduction', rentACarId: rentACarId }));
  }

  function handleSortByRating() {
    dispatch(sortVehicles({ by: 'rating', rentACarId: rentACarId }));
  }

  return (
    <Container maxWidth="xl">
      <Modal open={createModalVisibility}>
        <div
          className="modal-container-sm"
          style={modalStyle}
          className={classes.paper}
        >
          <CreateVehicle rentACarId={rentACarId} closeModal={closeModal} />
          <Button onClick={closeModal}>Close</Button>
        </div>
      </Modal>
      <Grid container spacing={1}>
        <Grid item xl={2}>
          <h2>Vehicles</h2>
        </Grid>
        {user.role === 'RENT_A_CAR_ADMIN' ? (
          <Grid mx={4} className={classes.addPosition}>
            <Icon onClick={() => setCreateModalVisibility(true)}>
              add_circle
            </Icon>
          </Grid>
        ) : null}
      </Grid>
      <Grid container spacing={1}>
        <Grid item xl={6}>
          <TextField
            required
            label="Number of people"
            className={classes.textField}
            margin="normal"
            type="number"
            onChange={({ currentTarget }) => setSeats(currentTarget.value)}
          />
          <TextField
            required
            label="Pick up location"
            className={classes.textField}
            margin="normal"
            onChange={({ currentTarget }) =>
              setPickUpLocation(currentTarget.value)
            }
          />
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              disableToolbar
              minDate={new Date()}
              variant="inline"
              format="dd/MM/yyyy"
              margin="normal"
              label="Pick up date"
              value={pickUpDate}
              onChange={date => setPickUpDate(dateFormat(date, 'yyyy-mm-dd'))}
              KeyboardButtonProps={{
                'aria-label': 'change date'
              }}
            />
          </MuiPickersUtilsProvider>
          <TextField
            label="Price from"
            defaultValue={price.min}
            className={classes.textField}
            onChange={({ currentTarget }) =>
              setPrice(currState => ({
                ...currState,
                min: currentTarget.value
              }))
            }
            margin="normal"
            type="number"
          />
        </Grid>
        <Grid item xl={6}>
          <TextField
            required
            label="Type"
            className={classes.textField}
            margin="normal"
            onChange={({ currentTarget }) => setType(currentTarget.value)}
          />
          <TextField
            required
            label="Drop off location"
            className={classes.textField}
            margin="normal"
            onChange={({ currentTarget }) =>
              setDropOffLocation(currentTarget.value)
            }
          />
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              disableToolbar
              minDate={new Date()}
              variant="inline"
              format="dd/MM/yyyy"
              margin="normal"
              label="Drop off date"
              value={dropOffDate}
              onChange={date => setDropOffDate(dateFormat(date, 'yyyy-mm-dd'))}
              KeyboardButtonProps={{
                'aria-label': 'change date'
              }}
            />
          </MuiPickersUtilsProvider>
          <TextField
            label="Price to"
            defaultValue={price.max}
            className={classes.textField}
            onChange={({ currentTarget }) =>
              setPrice(currState => ({
                ...currState,
                max: currentTarget.value
              }))
            }
            margin="normal"
            type="number"
          />
        </Grid>
        <div class="mb-4">
          <Grid item xs={3}>
            <Button
              justify="center"
              variant="contained"
              color="primary"
              className={classes.button}
              onClick={handleSearch}
            >
              Search
            </Button>
          </Grid>
        </div>
      </Grid>
      <ButtonGroup
        size="small"
        aria-label="small outlined button group"
        className={classes.buttonGroupPosition}
      >
        <Button disabled>SORT BY</Button>
        <Button className={classes.buttonGroup} onClick={handleSortByBrand}>
          BRAND
        </Button>
        <Button className={classes.buttonGroup} onClick={handleSortByModel}>
          MODEL
        </Button>
        <Button className={classes.buttonGroup} onClick={handleSortByYop}>
          YEAR OF PRODUCTION
        </Button>
        <Button className={classes.buttonGroup} onClick={handleSortByRating}>
          RATING
        </Button>
      </ButtonGroup>
      <Box display="flex" p={1} className={classes.bckg}>
        {Object.keys(vehicles).map(vehicleId => (
          <RentACarVehicle
            key={vehicleId}
            vehicle={vehicles[vehicleId]}
            airplaneTicket={airplaneTicket}
          />
        ))}
      </Box>
    </Container>
  );
}

function getModalStyle() {
  const top = 50;
  const left = 50;

  return {
    top: `${top}%`,
    left: `${left}%`,
    transform: `translate(-${top}%, -${left}%)`
  };
}

const useStyles = makeStyles(theme => ({
  paper: {
    position: 'absolute',
    width: 400,
    backgroundColor: theme.palette.background.paper,
    border: '2px solid #000',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3)
  },
  bckg: {
    backgroundImage: `url(${Background})`,
    textAlign: 'center'
  },
  root: {
    position: 'absolute',
    left: '20%',
    top: '5%',
    width: '60%',
    display: 'flex',
    flexDirection: 'column'
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    flex: 1
  },
  margin: {
    margin: theme.spacing(1)
  },
  button: {
    margin: theme.spacing(1),
    width: '30%',
    marginLeft: 'auto',
    background: '#008080',
    color: '#FFFFFF'
  },
  buttonGroup: {
    background: '#008080',
    color: '#FFFFFF'
  },
  buttonGroupPosition: {
    background: '#008080',
    marginTop: 25,
    marginBottom: 25
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120
  },
  selectEmpty: {
    marginTop: theme.spacing(2)
  },
  listScroll: {
    maxHeight: '370px',
    overflow: 'scroll'
  },
  addPosition: {
    marginTop: 25
  },
  largeIcon: {
    fontSize: '3em'
  }
}));
