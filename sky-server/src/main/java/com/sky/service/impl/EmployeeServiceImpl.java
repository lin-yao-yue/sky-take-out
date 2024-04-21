package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());  // 对前端传入的密码进行md5加密处理
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    // 新增员工
    @Override
    public void save(EmployeeDTO employeeDTO) {
        /*
        * 如果前端传入的数据和entity差别比较大(大部分属性都没有)
        * 那就建议用DTO接收前端传入的json数据
        * 到service层后，再将DTO转换为entity
        *   由于dto和entity有属性的重叠，所以可以使用属性拷贝来创建entity对象
        * */
        Employee employee = new Employee();
        // 对象属性拷贝 (源,目标), 前提是属性名必须一致
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置账号状态
        employee.setStatus(StatusConstant.ENABLE);
        // 设置密码 默认为123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        /*
        * 由于使用了aop，所以不需要再手动赋值
        * */

        // 设置时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        /*
        * 设置当前记录的创建人，修改人id
        * 使用 ThreadLocal 访问登录的时候存储在线程空间中的 id
        *   在拦截器中存进去，在service中取出来
        * */
        //employee.setCreateUser(BaseContext.getCurrentId());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    // 分页查询
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        /*
        * 开始分页查询, 传入：第几页，每一页要显示的数量
        * 底层：
        *   将要查询的 页码和每页包含的数量 封装到对象中，存储在ThreadLocal的线程空间中
        *   先 mybatis的动态sql查询所有符合条件的数据量
        *   再 将ThreadLocal中的两个参数，动态地拼接到sql中的 limit(起始页码,该页包含的数据量) 参数中
        * */
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = page.getTotal();  // 所有页的总记录数 自动执行 select count(0) from xxx
        List<Employee> records = page.getResult();  // 当前页的数据集合，pageHelper按照上述注释过程自动计算
        return new PageResult(total, records);
    }

    // 启用禁用员工
    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.update(employee);
    }

    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("****");  // 修改返回的密码
        return employee;
    }


    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 通过属性拷贝赋值
        BeanUtils.copyProperties(employeeDTO, employee);
        // 修改时间和修改人 使用aop替代
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

}
