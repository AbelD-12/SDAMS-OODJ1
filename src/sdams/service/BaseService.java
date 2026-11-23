package sdams.service;

import sdams.util.FileHandler;
import sdams.util.Searchable;
import sdams.util.Storable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseService<T extends Storable>
        implements Searchable<T> {

    protected abstract String getFileName();

    protected abstract T parse(String line);

    @Override
    public List<T> search(String keyword) {
        List<T> results = new ArrayList<>();

        if (keyword == null) {
            keyword = "";
        }

        String searchTerm = keyword.toLowerCase();

        for (T item : getAll()) {
            if (item.toString().toLowerCase().contains(searchTerm)) {
                results.add(item);
            }
        }

        return results;
    }

    public List<T> getAll() {

        List<T> result = new ArrayList<>();

        List<String> lines = FileHandler.readLines(getFileName());

        for (String line : lines) {

            if (line.trim().isEmpty()) {
                continue;
            }

            result.add(parse(line));
        }

        return result;
    }

    public T getById(String id) {

        for (T item : getAll()) {

            if (item.getId().equals(id)) {
                return item;
            }
        }

        return null;
    }

    public void add(T item) {

        FileHandler.appendLine(
                getFileName(),
                item.toFileString()
        );
    }

    public void update(T item) {

        List<T> items = getAll();

        boolean found = false;

        for (int i = 0; i < items.size(); i++) {

            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                found = true;
                break;
            }
        }

        if (!found) {
            return;
        }

        List<String> lines = new ArrayList<>();

        for (T current : items) {
            lines.add(current.toFileString());
        }

        FileHandler.writeLines(
                getFileName(),
                lines
        );
    }

    public void delete(String id) {

        List<T> items = getAll();

        items.removeIf(item ->
                item.getId().equals(id)
        );

        List<String> lines = new ArrayList<>();

        for (T item : items) {
            lines.add(item.toFileString());
        }

        FileHandler.writeLines(
                getFileName(),
                lines
        );
    }
}