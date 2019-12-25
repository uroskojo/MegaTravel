import React from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import RegistrationPage from './pages/Registration';
import HotelProfilePage from './pages/HotelProfile';
import PrivateRoute from './components/UI/PrivateRoute';
import AirlineProfile from './pages/AirlineProfile';
import HotelsPage from './pages/Hotels';
import HotelRoomsPage from './pages/HotelRooms';
import UserProfile from './pages/UserProfile';
import TicketReservation from './components/airplane_ticket/TicketReservation';
import ChooseSeats from './components/airplane_ticket/ChooseSeats';
import Search from './components/user/Search';
import AdminPage from './pages/AdminPage';
import RentACarPage from './pages/RentACars';
import RentACarProfilePage from './pages/RentACarProfile';
import PasswordUpdatePage from './pages/PasswordChangePage';
import RACAdminProfilePage from './pages/RACAdminProfile';
import InvitePage from './pages/Invites';
import Friends from './components/user/Friends';

const App = () => {
  return (
    <Switch>
      <Route exact path="/" component={HomePage} />
      <Route exact path="/register" component={RegistrationPage} />
      <PrivateRoute
        exact
        path="/user/hotel/:id"
        component={HotelProfilePage}
        accessRole="HOTEL_ADMIN"
      />

      <Route exact path="/airline/:id" component={AirlineProfile} />
      <Route exact path="/airlines" component={() => <h1>Airlines</h1>} />
      <Route exact path="/login" component={LoginPage} />
      <Route exact path="/hotel-reservation" component={HotelsPage} />
      <Route exact path="/ticket-reservation" component={TicketReservation} />

      <Route
        exact
        path="/ticket-reservation/:flight_id/choose-seat"
        component={ChooseSeats}
      />
      <Route
        exact
        path="/ticket-reservation/:flight_id/choose-seat/search/:requestType"
        component={Search}
      />
      <Route exact path="/rent-a-cars" component={RentACarPage} />
      <Route
        exact
        path="/rent-a-cars/:id/vehicles"
        component={RentACarProfilePage}
      />
      <Route exact path="/airline/:id" component={AirlineProfile} />
      <Route
        exact
        path="/hotel-reservation/:id/rooms"
        component={HotelRoomsPage}
      />
      <Route exact path="/user/:id" component={UserProfile} />
      <Route exact path="/user/:id/invites" component={InvitePage} />
      <Route exact path="/user/:id/friends" component={Friends} />
      <Route exact path="/admin" component={AdminPage} />
      <Route
        exact
        path="/rent-a-car-admin/update-password"
        component={PasswordUpdatePage}
        role={'RENT_A_CAR_ADMIN'}
      />
      <Route
        exact
        path="/rent-a-car-admin/:id"
        component={RACAdminProfilePage}
        role={'RENT_A_CAR_ADMIN'}
      />

      <Route
        exact
        path="/page-not-found"
        component={() => <h1>Page not found</h1>}
      />
      <Redirect from="*" to="/page-not-found" />
    </Switch>
  );
};

export default App;
