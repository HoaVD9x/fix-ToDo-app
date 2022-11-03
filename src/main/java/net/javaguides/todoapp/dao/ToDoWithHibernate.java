package net.javaguides.todoapp.dao;

import net.javaguides.todoapp.model.Todo;
import net.javaguides.todoapp.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ToDoWithHibernate implements TodoDao {

    Session session;

    Transaction transaction = null;


    @Override
    public void insertTodo(Todo todo) throws SQLException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(todo);
            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    @Override
    public Todo selectTodo(long todoId) {
        Todo todo = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Todo> criteriaQuery = builder.createQuery(Todo.class);
            Root<Todo> rootSelectToDo = criteriaQuery.from(Todo.class);
            criteriaQuery.select(rootSelectToDo).where(builder.equal(rootSelectToDo.get("id"), todoId));
            todo = session.createQuery(criteriaQuery).getSingleResult();
            Todo emptyTodo = new Todo();
            todo.setTitle(emptyTodo.getTitle());
            todo.setDescription(emptyTodo.getDescription());
            todo.setStatus(emptyTodo.getStatus());
            todo.setTargetDate(emptyTodo.getTargetDate());
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return todo;
    }

    @Override
    public List<Todo> selectAllTodos() {
        List<Todo> todoList = new ArrayList<>();

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Todo> criteriaQuery = builder.createQuery(Todo.class);
            Root<Todo> root = criteriaQuery.from(Todo.class);
            todoList = session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todoList;
    }

    @Override
    public boolean deleteTodo(int id) throws SQLException {
        boolean rowdelete;

        try (Session session = HibernateUtil.getSessionFactory().openSession();) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<Todo> criteriaDelete = builder.createCriteriaDelete(Todo.class);
            Root<Todo> rootDelete = criteriaDelete.from(Todo.class);
            criteriaDelete.where(builder.equal(rootDelete.get("id"), id));
            transaction = session.beginTransaction();
            rowdelete = session.createQuery(criteriaDelete).executeUpdate() > 0;
            transaction.commit();
        }
        return rowdelete;
    }

    @Override
    public boolean updateTodo(Todo todo) throws SQLException {
        boolean rowUpdate;

        try (Session session = HibernateUtil.getSessionFactory().openSession();) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate criteriaUpdate = builder.createCriteriaUpdate(Todo.class);
            Root<Todo> rootUpDate = criteriaUpdate.from(Todo.class);
            criteriaUpdate.set("title", todo.getTitle()).where(builder.equal(rootUpDate.get("id"), todo.getId()));
            criteriaUpdate.set("description", todo.getDescription()).where(builder.equal(rootUpDate.get("id"), todo.getId()));
            criteriaUpdate.set("targetDate", todo.getTargetDate()).where(builder.equal(rootUpDate.get("id"), todo.getId()));
            criteriaUpdate.set("status", todo.getStatus()).where(builder.equal(rootUpDate.get("id"), todo.getId()));
            transaction = session.beginTransaction();
            rowUpdate = session.createQuery(criteriaUpdate).executeUpdate() > 0;
            transaction.commit();
        }
        return rowUpdate;
    }
}
