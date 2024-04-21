package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into " +
            "employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user, status)" +
            "values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
    void insert(Employee employee);

    /*
    * 分页查询
    * 由于会使用到动态标签，所以用动态sql
    * */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /*
    * 根据主键动态修改属性
    * 不再只是可以修改某个特定的属性
    * */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    // 根据id查询员工信息
    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);
}
