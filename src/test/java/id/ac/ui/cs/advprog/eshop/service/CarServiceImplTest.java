package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCar() {
        Car car = new Car();
        car.setCarId("C001");
        car.setCarName("Toyota");
        car.setCarColor("Red");
        car.setCarQuantity(5);

        when(carRepository.create(car)).thenReturn(car);

        Car createdCar = carService.create(car);

        assertNotNull(createdCar);
        assertEquals("C001", createdCar.getCarId());
        verify(carRepository, times(1)).create(car);
    }

    @Test
    void testFindAll() {
        List<Car> carList = new ArrayList<>();
        Car car1 = new Car(); car1.setCarId("C001");
        Car car2 = new Car(); car2.setCarId("C002");
        carList.add(car1);
        carList.add(car2);

        Iterator<Car> carIterator = carList.iterator();
        when(carRepository.findAll()).thenReturn(carIterator);

        List<Car> result = carService.findAll();

        assertEquals(2, result.size());
        assertEquals("C001", result.get(0).getCarId());
        assertEquals("C002", result.get(1).getCarId());
    }

    @Test
    void testFindByIdExisting() {
        Car car = new Car();
        car.setCarId("C001");
        when(carRepository.findById("C001")).thenReturn(car);

        Car foundCar = carService.findById("C001");

        assertNotNull(foundCar);
        assertEquals("C001", foundCar.getCarId());
    }

    @Test
    void testFindByIdNonExisting() {
        when(carRepository.findById("C999")).thenReturn(null);

        Car foundCar = carService.findById("C999");
        assertNull(foundCar);
    }

    @Test
    void testUpdateCar() {
        Car car = new Car();
        car.setCarId("C001");
        car.setCarName("Honda");
        car.setCarColor("Blue");
        car.setCarQuantity(10);

        when(carRepository.update("C001", car)).thenReturn(car); // Corrected

        carService.update("C001", car);

        verify(carRepository, times(1)).update("C001", car);
    }


    @Test
    void testDeleteCarById() {
        doNothing().when(carRepository).delete("C001");

        carService.deleteCarById("C001");

        verify(carRepository, times(1)).delete("C001");
    }
}
