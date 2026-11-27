package sdams.service;

import sdams.enums.EquipmentCondition;
import sdams.model.Equipment;
import sdams.util.IdGenerator;
import sdams.util.InvalidInputException;

public class EquipmentService extends BaseService<Equipment> {

    protected String getFileName() {
        return "data/equipment.txt";
    }

    protected Equipment parse(String line) {
        return Equipment.fromFileString(line);
    }

    public void addEquipment(String name, int quantity,
                             EquipmentCondition condition) {
        validateDetails(name, quantity, condition);

        String id = IdGenerator.next("EQP", getFileName());
        Equipment equipment = new Equipment(id, name.trim(), quantity, condition);
        add(equipment);
    }

    public void updateEquipment(String id, String name, int quantity,
                                EquipmentCondition condition) {
        if (getById(id) == null) {
            throw new InvalidInputException("Equipment item not found.");
        }

        validateDetails(name, quantity, condition);
        Equipment equipment = new Equipment(id, name.trim(), quantity, condition);
        update(equipment);
    }

    private void validateDetails(String name, int quantity,
                                 EquipmentCondition condition) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("Enter an equipment name.");
        }
        if (name.contains("|") || name.contains("\n") || name.contains("\r")) {
            throw new InvalidInputException("Use a single-line name without the | character.");
        }
        if (quantity < 0) {
            throw new InvalidInputException("Quantity must be zero or more.");
        }
        if (condition == null) {
            throw new InvalidInputException("Select an equipment condition.");
        }
    }
}