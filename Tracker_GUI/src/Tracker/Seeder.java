package Tracker;

import java.io.Serializable;

/**
 * Created by Ishtiaq Niloy on 07-Dec-16.
 */
public class Seeder  implements Serializable {
    String ipAddress;
    int port;

    Seeder(String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
    }

}
