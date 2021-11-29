package ru.job4j.chat.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UpdateFieldsPartially {

    public <T extends Model> T updateFieldsPartially(
            CrudRepository<T, Integer> repository, T model)
            throws InvocationTargetException, IllegalAccessException {

        Optional<T> optionalCurrent = repository.findById(model.getId());
        T current;
        if (optionalCurrent.isPresent()) {
            current = optionalCurrent.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Method[] methods = current.getClass().getDeclaredMethods();
        Map<String, Method> namePerMethod = new HashMap<>();
        for (Method method: methods) {
            String name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (String name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                Method getMethod = namePerMethod.get(name);
                Method setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid properties mapping");
                }
                Object newValue = getMethod.invoke(model);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }
        return repository.save(current);
    }

}