package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {
    @Test
    void testCarGettersAndSetters() {
        Car car = new Car();

        car.setCarId("C001");
        car.setCarName("Toyota");
        car.setCarColor("Red");
        car.setCarQuantity(10);

        assertEquals("C001", car.getCarId());
        assertEquals("Toyota", car.getCarName());
        assertEquals("Red", car.getCarColor());
        assertEquals(10, car.getCarQuantity());
    }

    @Test
    void testCarDefaultConstructor() {
        Car car = new Car();
        assertNotNull(car);
    }
}
