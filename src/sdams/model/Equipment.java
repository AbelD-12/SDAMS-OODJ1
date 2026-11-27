package sdams.model;

import sdams.enums.EquipmentCondition;
import sdams.util.Storable;

/**
 * Represents one item in the equipment inventory.
 */
public class Equipment implements Storable {

    private final String id;
    private final String name;
    private final int quantity;
    private final EquipmentCondition condition;

    public Equipment(String id, String name, int quantity,
                     EquipmentCondition condition) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.condition = condition;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public EquipmentCondition getCondition() {
        return condition;
    }

    public String toFileString() {
        return id + "|" + name + "|" + quantity + "|" + condition.name();
    }

    public static Equipment fromFileString(String line) {
        String[] fields = line.split("\\|", -1);

        if (fields.length != 4) {
            throw new IllegalArgumentException("Invalid equipment record.");
        }

        return new Equipment(
                fields[0],
                fields[1],
                Integer.parseInt(fields[2]),
                EquipmentCondition.valueOf(fields[3])
        );
    }

    public String toString() {
        return toFileString();
    }
}