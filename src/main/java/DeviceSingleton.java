import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class DeviceSingleton {
    private static DeviceSingleton instance = null;
    private static HashMap<UUID, List<Long>> persistance;

    private DeviceSingleton(){}

    static DeviceSingleton getInstance() {
        if(persistance == null) {
            persistance = new HashMap<UUID, List<Long>>();
        }

        if(instance == null) {
            instance = new DeviceSingleton();
        }
        return instance;
    }

    Boolean putDevice(String device, String time) {
        Long leTime = Long.parseLong(time);
        UUID leUuid = UUID.fromString(device);

        if(persistance.get(leUuid) == null) {
            List<Long> leList = new LinkedList<>();
            persistance.put(leUuid, leList);
            persistance.get(leUuid).add(leTime);
        } else {
            persistance.get(leUuid).add(leTime);
        }

        return true;
    }

    Boolean clearDevices() {
        persistance.clear();
        return true;
    }

    List<UUID> getDevices() {
        List<UUID> devices = new LinkedList<UUID>();
        devices.addAll(persistance.keySet());
        return devices;
    }

    List<Long> getDeviceOnDate(String device, String date) throws ParseException {
        final UUID leUuid = UUID.fromString(device);
        final Long leStartFilter = leTimeConverter(date);
        final Long leEndFilter = leStartFilter + 86400;

        return persistance
                .get(leUuid)
                .stream()
                .filter(p -> (p >= leStartFilter && p < leEndFilter))
                .collect(Collectors.toList());
    }

    List<Long> getDeviceFromTo(String device, String from, String to) throws ParseException {
        final UUID leUuid = UUID.fromString(device);
        final Long leStartFilter = leTimeConverter(from);
        final Long leEndFilter = leTimeConverter(to);

        return persistance
                .get(leUuid)
                .stream()
                .filter(p -> (p >= leStartFilter && p < leEndFilter))
                .collect(Collectors.toList());
    }

    private List<Long> getDeviceFromTo(UUID device, Long from, Long to){
        return persistance
                .get(device)
                .stream()
                .filter(p -> (p >= from && p < to))
                .collect(Collectors.toList());
    }

    HashMap<UUID, List<Long>> getAllFromTo(String from, String to) throws ParseException {
        final Long leStartFilter = leTimeConverter(from);
        final Long leEndFilter = leTimeConverter(to);

        HashMap<UUID, List<Long>> leHashMap = new HashMap<UUID, List<Long>>();
        for(UUID key : persistance.keySet() ) {
            leHashMap.put(key, getDeviceFromTo(key, leStartFilter, leEndFilter));
        }

        return leHashMap;
    }

    private Long leTimeConverter(String date) throws ParseException {
        if(StringUtils.isNumeric(date)){
            return Long.parseLong(date);
        } else {
            SimpleDateFormat leDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            leDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return (long)(leDateFormat.parse(date).getTime() * 0.001);
        }
    }
}
