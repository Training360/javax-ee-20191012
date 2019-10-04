package empapp;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class EmployeeDao {

    @PersistenceContext
    private EntityManager em;

    @Resource
    TransactionSynchronizationRegistry registry;


    public void insertEmployee(Employee employee) {
        em.persist(employee);
//        registry.setRollbackOnly();
    }

    public void updateEmployee(long id, String name) {
        Employee employee = em.find(Employee.class, id);
        employee.setName(name);
    }

    public List<Employee> listEmployees() {
        return em.createQuery("select e from Employee e", Employee.class)
                .getResultList();
    }
}
