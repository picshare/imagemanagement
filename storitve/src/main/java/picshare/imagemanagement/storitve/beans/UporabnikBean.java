package picshare.imagemanagement.storitve.beans;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import picshare.imagemanagement.entitete.jpa.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
public class UporabnikBean {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    @PostConstruct
    private void init() {
        log.info("UporabnikBean initialized");
    }

    @PersistenceContext(unitName = "picshare-userservice-jpa")
    private EntityManager em;

    public List<User> getAllUsers(QueryParameters query) {
        try {
            List<User> allUsers = JPAUtils.queryEntities(em, User.class, query);
            return allUsers;
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    public User getUser(int idUser) {
        try {
            return em.find(User.class, idUser);
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public User addUser(User user) {
        try {
            em.persist(user);
            em.flush();
            log.info(String.format("Added User(%s, %s, %s, %s)", user.getName(), user.getSurname(),
                    user.getUsername(), user.getEmail()));
            return user;
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public void updateUser(int idUser, User user) {
        try {
            User userFound = em.find(User.class, idUser);
            userFound.setUsername(user.getUsername());
            userFound.setEmail(user.getEmail());
            userFound.setSurname(user.getSurname());
            userFound.setName(user.getName());
            em.merge(userFound);
        } catch (Exception e) {
            log.warning(e.toString());
        }
    }

    @Transactional
    public boolean deleteUser(int idUser) {
        try {
            User user = em.find(User.class, idUser);
            log.info(user.getName());
            em.remove(user);
            return true;
        } catch (Exception e) {
            log.warning(e.toString());
            return false;
        }
    }

    @Transactional
    public void deleteAllUsers() {
        try {
            Query q = em.createNamedQuery("User.deleteAll");
            q.executeUpdate();
        } catch (Exception e) {
            log.warning(e.toString());
        }
    }

    public List<User> getAllBySurnameUser(String surname) {
        try {
            Query q = em.createNamedQuery("User.getAllBySurname");
            q.setParameter("surname", surname);
            return (List<User>) (q.getResultList());
        } catch (Exception e) {
            log.warning(e.toString());
            return null;
        }
    }

    @Transactional
    public void deleteAllBySurnameUser(String surname) {
        try {
            Query q = em.createNamedQuery("User.deleteAllBySurname");
            q.setParameter("surname", surname);
            q.executeUpdate();
        } catch (Exception e) {
            log.warning(e.toString());
        }
    }
}
