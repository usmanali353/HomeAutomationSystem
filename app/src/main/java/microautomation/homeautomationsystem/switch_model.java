package microautomation.homeautomationsystem;


public class switch_model{

    String switch_name;
    int switch_state;
   public switch_model(){

   }
    public String getSwitch_name() {
        return switch_name;
    }

    public void setSwitch_name(String switch_name) {
        this.switch_name = switch_name;
    }

    public int getSwitch_state() {
        return switch_state;
    }

    public void setSwitch_state(int switch_state) {
        this.switch_state = switch_state;
    }

    public switch_model(String switch_name, int switch_state) {
        this.switch_name = switch_name;
        this.switch_state = switch_state;
    }

}
