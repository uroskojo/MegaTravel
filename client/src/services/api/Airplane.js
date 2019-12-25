import HttpBaseClient from "../HttpBaseClient";
import { format } from "util";

const ENDPOINTS = {
  SAVE: "/airplanes",
  FETCH: "/airplanes/%s"
};

class AirplaneService extends HttpBaseClient {
  save = airplane => {
    return this.getApiClient().put(ENDPOINTS.SAVE, airplane);
  };

  fetchAirplanes = airlineId => {
    return this.getApiClient().get(format(ENDPOINTS.FETCH, airlineId));
  };
}

export default new AirplaneService();
