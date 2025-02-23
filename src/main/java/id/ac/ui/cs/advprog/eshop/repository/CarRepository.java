package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CarRepository {
    private List<Car> carData = new ArrayList<>();

    // Create a new car
    public Car create(Car car) {
        if (car.getCarId() == null) {
            UUID uuid = UUID.randomUUID();
            car.setCarId(uuid.toString());
        }
        carData.add(car);
        return car;
    }

    // Retrieve all cars
    public List<Car> findAll() {
        return new ArrayList<>(carData); // Return a copy to avoid modification outside the repository
    }

    // Find a car by ID
    public Car findById(String id) {
        return carData.stream()
                .filter(car -> car.getCarId().equals(id))
                .findFirst()
                .orElse(null); // Returns null if not found
    }

    // Update a car
    public Car update(String id, Car updatedCar) {
        for (Car car : carData) {
            if (car.getCarId().equals(id)) {
                // Update existing car details
                car.setCarName(updatedCar.getCarName());
                car.setCarColor(updatedCar.getCarColor());
                car.setCarQuantity(updatedCar.getCarQuantity());
                return car; // Return the updated car
            }
        }
        return null; // Return null if car not found
    }

    // Delete a car by ID
    public void delete(String id) {
        carData.removeIf(car -> car.getCarId().equals(id));
    }
}
