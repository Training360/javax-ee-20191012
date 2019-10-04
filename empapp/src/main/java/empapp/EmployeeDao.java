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

@Transactional
    public void insertEmployee(Employee employee) {
        em.persist(employee);
//        registry.setRollbackOnly();
    }


    public void updateEmployee(long id, String name) {
        Employee employee = em.find(Employee.class, id);
        employee.setName(name);
    }

    public List<Employee> listEmployees() {
        return em.createNamedQuery("listEmployee", Employee.class)
                .getResultList();
    }

    public void addAddress(long id, String city) {
//        Employee employee = em.find(Employee.class, id);
//        Address address = new Address(city);
//        employee.addAddress(address);
//        em.persist(address);
        Employee employee = em.getReference(Employee.class, id);
        Address address = new Address(city);
        address.setEmployee(employee);
        em.persist(address);
    }

    public boolean existsEmployeeWithName(String name) {
        return em
                .createQuery("select count(e.id) from Employee e where e.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult() > 0;
    }
}
