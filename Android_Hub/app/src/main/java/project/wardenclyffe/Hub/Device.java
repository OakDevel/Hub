package project.wardenclyffe.Hub;

/**
 * Created by André on 06/08/15.
 */

public class Device {

    /**
     * Device's type
     * It can be a smart lamp, it can be a interruptor, and so on..
     */
    String Tipo;

    /**
     * Device state
     * True == ON
     * False == OFF
     */
    boolean Estado;

    /**
     * MAC Address of the device
     */
    String ID;

    public Device(String Tipo, boolean Estado, String ID) {
        this.Tipo = Tipo;
        this.Estado = Estado;
        this.ID = ID;
    }

    public void setEstado(boolean estado) {
        this.Estado = estado;
    }

    public boolean getEstado() {
        return Estado;
    }

    public String getTipo() {
        return Tipo;
    }

    public String getID() {
        return ID;
    }

}
