package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    // Helper method to create a Car object.
    private Car createCar(String carId, String carName, String carColor, int quantity) {
        Car car = new Car();
        car.setCarId(carId);
        car.setCarName(carName);
        car.setCarColor(carColor);
        car.setCarQuantity(quantity);
        return car;
    }

    // Test creating a car with a null id. The repository should generate an id.
    @Test
    void testCreateCarWithNullId() {
        Car car = createCar(null, "Toyota", "Red", 5);
        Car createdCar = carRepository.create(car);
        assertNotNull(createdCar.getCarId(), "Car ID should be auto-generated when null");
        assertEquals("Toyota", createdCar.getCarName());
        assertEquals("Red", createdCar.getCarColor());
        assertEquals(5, createdCar.getCarQuantity());
    }

    // Test creating a car with an already provided id.
    @Test
    void testCreateCarWithExistingId() {
        String id = "1234";
        Car car = createCar(id, "Honda", "Blue", 3);
        Car createdCar = carRepository.create(car);
        assertEquals(id, createdCar.getCarId(), "Car ID should remain unchanged if provided");
    }

    // Test findAll returns an empty iterator when there are no cars.
    @Test
    void testFindAllEmpty() {
        Iterator<Car> iterator = carRepository.findAll();
        assertFalse(iterator.hasNext(), "Iterator should have no elements when repository is empty");
    }

    // Test findAll returns all cars that have been created.
    @Test
    void testFindAllWithCars() {
        Car car1 = createCar(null, "Toyota", "Red", 5);
        Car car2 = createCar(null, "Honda", "Blue", 3);
        carRepository.create(car1);
        carRepository.create(car2);

        Iterator<Car> iterator = carRepository.findAll();
        int count = 0;
        while (iterator.hasNext()) {
            Car car = iterator.next();
            assertNotNull(car, "Each car in the iterator should not be null");
            count++;
        }
        assertEquals(2, count, "Repository should contain exactly 2 cars");
    }

    // Test that findById returns the correct car when it exists.
    @Test
    void testFindByIdFound() {
        Car car = createCar(null, "Toyota", "Red", 5);
        Car createdCar = carRepository.create(car);
        String id = createdCar.getCarId();
        Car foundCar = carRepository.findById(id);
        assertNotNull(foundCar, "Car should be found by its id");
        assertEquals("Toyota", foundCar.getCarName());
    }

    // Test that findById returns null when no car with the given id exists.
    @Test
    void testFindByIdNotFound() {
        Car foundCar = carRepository.findById("non-existent");
        assertNull(foundCar, "findById should return null if no car with the given id is found");
    }

    // Test updating an existing car.
    @Test
    void testUpdateCarFound() {
        Car car = createCar(null, "Toyota", "Red", 5);
        Car createdCar = carRepository.create(car);
        String id = createdCar.getCarId();

        // Create an updated car instance with new details.
        Car updatedInfo = createCar(null, "Toyota Updated", "Black", 10);
        Car updatedCar = carRepository.update(id, updatedInfo);

        assertNotNull(updatedCar, "Updated car should not be null");
        assertEquals("Toyota Updated", updatedCar.getCarName(), "Car name should be updated");
        assertEquals("Black", updatedCar.getCarColor(), "Car color should be updated");
        assertEquals(10, updatedCar.getCarQuantity(), "Car quantity should be updated");
    }

    // Test that updating a non-existent car id returns null.
    @Test
    void testUpdateCarNotFound() {
        Car updatedInfo = createCar(null, "Toyota Updated", "Black", 10);
        Car updatedCar = carRepository.update("non-existent", updatedInfo);
        assertNull(updatedCar, "Update should return null when the car id is not found");
    }

    // Test that deleting an existing car actually removes it from the repository.
    @Test
    void testDeleteCar() {
        Car car = createCar(null, "Toyota", "Red", 5);
        Car createdCar = carRepository.create(car);
        String id = createdCar.getCarId();

        // Verify that the car exists.
        assertNotNull(carRepository.findById(id), "Car should exist before deletion");

        // Delete the car.
        carRepository.delete(id);
        assertNull(carRepository.findById(id), "Car should be deleted and no longer found");
    }

    // Test that deleting a non-existent id does not affect the repository.
    @Test
    void testDeleteCarNotFound() {
        Car car = createCar(null, "Toyota", "Red", 5);
        carRepository.create(car);

        // Attempt to delete a car with a non-existent id.
        carRepository.delete("non-existent");

        // Repository should still contain the one added car.
        Iterator<Car> iterator = carRepository.findAll();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(1, count, "Deleting a non-existent car should not remove any car");
    }

    // Test: findById returns the car when provided a valid id.
    @Test
    void testFindByIdValid() {
        Car car = createCarManual(null, "Toyota", "Red", 5);
        // Using create() ensures that if carId is null, it will be auto-generated.
        Car createdCar = carRepository.create(car);
        String id = createdCar.getCarId();

        Car foundCar = carRepository.findById(id);
        assertNotNull(foundCar, "findById should return a car when a valid id is provided");
        assertEquals("Toyota", foundCar.getCarName());
    }

    // Test: findById returns null when searching for a non-existent id.
    @Test
    void testFindByIdNonExistent() {
        Car foundCar = carRepository.findById("non-existent-id");
        assertNull(foundCar, "findById should return null for an id that does not exist");
    }

    // Test: findById with null parameter when repository is empty.
    @Test
    void testFindByIdWithNullParamEmptyRepo() {
        Car foundCar = carRepository.findById(null);
        assertNull(foundCar, "findById should return null when passing null and repository is empty");
    }

    // Test: findById with null parameter when repository has valid cars.
    // Since all cars created via create() have non-null ids, searching for null should return null.
    @Test
    void testFindByIdWithNullParamNonEmptyRepo() {
        Car car = createCarManual(null, "Honda", "Blue", 3);
        carRepository.create(car); // Auto-generates an id if null.
        Car foundCar = carRepository.findById(null);
        assertNull(foundCar, "findById should return null when searching for null even if repository is non-empty");
    }

    // The following tests simulate an edge case where a car with a null id
    // is manually inserted into the repository. This bypasses the create() method.
    // In such a case, calling findById will trigger a NullPointerException when it attempts
    // to call equals() on a null id.

    // Test: Manually insert a car with a null id and search for null.
    @Test
    void testFindByIdWithCarHavingNullIdAndNullSearch() throws Exception {
        Car car = createCarManual(null, "Ford", "Green", 2);
        // Use reflection to directly access and modify the private 'carData' list.
        Field field = CarRepository.class.getDeclaredField("carData");
        field.setAccessible(true);
        List<Car> carData = (List<Car>) field.get(carRepository);
        // Manually add a car with a null id (bypassing the auto-generation in create()).
        carData.add(car);

        // Expect a NullPointerException since car.getCarId() is null
        assertThrows(NullPointerException.class, () -> {
            carRepository.findById(null);
        }, "findById should throw NullPointerException when a car with a null id is present and searched with null");
    }

    // Test: Manually insert a car with a null id and search with a non-null id.
    @Test
    void testFindByIdWithCarHavingNullIdAndNonNullSearch() throws Exception {
        Car car = createCarManual(null, "Ford", "Green", 2);
        Field field = CarRepository.class.getDeclaredField("carData");
        field.setAccessible(true);
        List<Car> carData = (List<Car>) field.get(carRepository);
        carData.add(car);

        // Even when searching with a non-null id, the first car (with null id) will cause a NullPointerException.
        assertThrows(NullPointerException.class, () -> {
            carRepository.findById("some-id");
        }, "findById should throw NullPointerException when a car with a null id is present, regardless of the search parameter");
    }

    private Car createCarManual(String id, String name, String color, int quantity) {
        Car car = new Car();
        car.setCarId(id);
        car.setCarName(name);
        car.setCarColor(color);
        car.setCarQuantity(quantity);
        return car;
    }

    // Normal Case: Update an existing car.
    @Test
    void testUpdateExistingCar() {
        // Create a car via create() so that if id is null, an id is auto-generated.
        Car originalCar = createCarManual(null, "Toyota", "Red", 5);
        Car createdCar = carRepository.create(originalCar);
        String id = createdCar.getCarId();

        // Create update details.
        Car updateInfo = createCarManual(null, "Honda", "Blue", 10);
        Car updatedCar = carRepository.update(id, updateInfo);

        assertNotNull(updatedCar, "Updated car should not be null");
        assertEquals("Honda", updatedCar.getCarName(), "Car name should be updated");
        assertEquals("Blue", updatedCar.getCarColor(), "Car color should be updated");
        assertEquals(10, updatedCar.getCarQuantity(), "Car quantity should be updated");
    }

    // Edge Case: Attempt to update a non-existent car id.
    @Test
    void testUpdateNonExistingCar() {
        Car updateInfo = createCarManual(null, "Nissan", "Green", 7);
        Car result = carRepository.update("non-existent-id", updateInfo);
        assertNull(result, "Update should return null when the car id is not found");
    }

    // Edge Case: Passing a null updatedCar should trigger a NullPointerException.
    @Test
    void testUpdateWithNullUpdatedCar() {
        Car originalCar = createCarManual(null, "Toyota", "Red", 5);
        Car createdCar = carRepository.create(originalCar);
        String id = createdCar.getCarId();

        // Expect a NullPointerException when updatedCar is null
        assertThrows(NullPointerException.class, () -> {
            carRepository.update(id, null);
        }, "Updating with a null updatedCar should throw a NullPointerException");
    }

    // Edge Case: Passing a null id when the repository contains valid cars.
    // Since the valid car has a non-null id, no match is found and update returns null.
    @Test
    void testUpdateWithNullIdInNonEmptyRepo() {
        Car originalCar = createCarManual(null, "Toyota", "Red", 5);
        carRepository.create(originalCar);
        Car updateInfo = createCarManual(null, "Honda", "Blue", 10);

        Car result = carRepository.update(null, updateInfo);
        assertNull(result, "Update should return null when searching with null id and no matching car is found");
    }

    // Edge Case: Manually inject a car with a null id and call update.
    // Since update calls car.getCarId().equals(id), this will throw a NullPointerException.
    @Test
    void testUpdateWhenCarWithNullIdExists() throws Exception {
        // Manually create a car with a null id.
        Car carWithNullId = createCarManual(null, "Ford", "Green", 3);
        // Use reflection to directly add the car into the private carData list.
        Field field = CarRepository.class.getDeclaredField("carData");
        field.setAccessible(true);
        List<Car> carData = (List<Car>) field.get(carRepository);
        carData.add(carWithNullId);

        // Prepare update details.
        Car updateInfo = createCarManual(null, "Chevrolet", "Yellow", 8);

        // Expect a NullPointerException because carWithNullId.getCarId() is null.
        assertThrows(NullPointerException.class, () -> {
            carRepository.update(null, updateInfo);
        }, "Update should throw a NullPointerException when a car with a null id exists and search id is null");
    }

}
