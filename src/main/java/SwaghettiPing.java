import java.util.HashMap;

import static spark.Spark.*;

public class SwaghettiPing {
    private static boolean checkDevice(String device) {
        return device.matches("[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}");
    }

    public static void main(String[] args) {
        DeviceSingleton devices = DeviceSingleton.getInstance();

        // Get All between dates
        get("/all/:from/:to", (req, res) -> {
            return devices.getAllFromTo(req.params(":from"), req.params(":to"));
        }, new JsonTransformer());

        // Post to Server with Ping
        post("/:device_id/:epoch_time", (req, res) -> {
            if(!checkDevice(req.params(":device_id"))) return new HashMap<>();
            return devices.putDevice(req.params(":device_id"), req.params(":epoch_time"));
        }, new JsonTransformer());

        // Get Device on a Date
        get("/:device_id/:date", (req, res) -> {
            if(!checkDevice(req.params(":device_id"))) return new HashMap<>();
            return devices.getDeviceOnDate(req.params(":device_id"), req.params(":date"));
        }, new JsonTransformer());

        // Get Device Between Dates
        get("/:device_id/:from/:to", (req, res) -> {
            if(!checkDevice(req.params(":device_id"))) return new HashMap<>();
            return devices.getDeviceFromTo(req.params(":device_id"), req.params(":from"), req.params(":to"));
        }, new JsonTransformer());

        // Get list of Devices
        get("/devices", (req, res) -> {
            return devices.getDevices();
        }, new JsonTransformer());

        // POST to clear all data
        post("/clear_data", (req, res) -> {
            return devices.clearDevices();
        }, new JsonTransformer());
    }
}
