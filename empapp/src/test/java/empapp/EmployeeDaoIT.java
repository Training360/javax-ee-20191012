package empapp;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class EmployeeDaoIT {

    @Inject
    private EmployeeDao employeeDao;

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive webArchive =
                ShrinkWrap.create(WebArchive.class)
                .addClasses(EmployeeDao.class, Employee.class, Address.class)
                .addAsResource("META-INF/persistence.xml");
        System.out.println(webArchive);
        return webArchive;
    }

    @Test
    public void existsShouldReturnFalse() {
        assertFalse(employeeDao.existsEmployeeWithName("John Doe"));
    }

    @Test
    public void existsShouldReturnTrue() {
        employeeDao.insertEmployee(new Employee("John Doe"));
        assertTrue(employeeDao.existsEmployeeWithName("John Doe"));
    }
}
