package microautomation.homeautomationsystem;

public class Device {
    String device_name;

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public Device() {

    }

    public Device(String device_name) {
        this.device_name = device_name;
    }
}
