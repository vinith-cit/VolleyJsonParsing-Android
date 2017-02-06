package vinith.com.zipgo;

import org.json.JSONArray;

/**
 * Created by liveongo on 24/9/16.
 */
public class Routes {
    private int id;
    private String name;
    private String description;
    private JSONArray stops_sequence;

    public Routes(int id ,String name,String description,JSONArray stops_sequence) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stops_sequence = stops_sequence;

    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONArray getStops_sequence() {
        return stops_sequence;
    }

    public void setStops_sequence(JSONArray stops_sequence) {
        this.stops_sequence = stops_sequence;
    }
}
