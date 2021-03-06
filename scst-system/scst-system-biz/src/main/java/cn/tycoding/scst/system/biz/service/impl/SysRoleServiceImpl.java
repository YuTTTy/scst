package cn.tycoding.scst.system.biz.service.impl;

import cn.tycoding.scst.common.core.utils.QueryPage;
import cn.tycoding.scst.system.api.entity.SysRole;
import cn.tycoding.scst.system.api.entity.SysRoleMenu;
import cn.tycoding.scst.system.api.entity.SysRoleWithMenu;
import cn.tycoding.scst.system.biz.mapper.SysRoleMapper;
import cn.tycoding.scst.system.biz.mapper.SysRoleMenuMapper;
import cn.tycoding.scst.system.biz.service.SysRoleMenuService;
import cn.tycoding.scst.system.biz.service.SysRoleService;
import cn.tycoding.scst.system.biz.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tycoding
 * @date 2019-06-02
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public List<SysRole> findUserRoles(String username) {
        return sysRoleMapper.findUserRoles(username);
    }

    @Override
    public IPage<SysRole> list(SysRole role, QueryPage queryPage) {
        IPage<SysRole> page = new Page<>(queryPage.getPage(), queryPage.getLimit());
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(role.getName()), SysRole::getName, role.getName());
        queryWrapper.orderByDesc(SysRole::getCreateTime);
        return sysRoleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public SysRoleWithMenu findById(Long id) {
        List<SysRoleWithMenu> list = sysRoleMapper.findById(id);
        List<Long> menuIds = list.stream().map(SysRoleWithMenu::getMenuId).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        SysRoleWithMenu sysRoleWithMenu = list.get(0);
        sysRoleWithMenu.setMenuIds(menuIds);
        return sysRoleWithMenu;
    }

    @Override
    @Transactional
    public void add(SysRoleWithMenu role) {
        role.setCreateTime(new Date());
        this.save(role);
        saveRoleMenu(role);
    }

    private void saveRoleMenu(SysRoleWithMenu role) {
        if (role.getMenuIds() != null && role.getMenuIds().get(0) != null) {
            role.getMenuIds().forEach(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setMenuId(menuId);
                roleMenu.setRoleId(role.getId());
                sysRoleMenuMapper.insert(roleMenu);
            });
        }
    }

    @Override
    @Transactional
    public void update(SysRoleWithMenu role) {
        this.updateById(role);
        sysRoleMenuService.deleteRoleMenusByRoleId(role.getId());
        this.saveRoleMenu(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sysRoleMapper.deleteById(id);
        sysRoleMenuService.deleteRoleMenusByRoleId(id);
        sysUserRoleService.deleteUserRolesByRoleId(id);
    }

    @Override
    public boolean checkName(String name, String id) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(id)) {
            queryWrapper.eq(SysRole::getName, name);
            queryWrapper.ne(SysRole::getId, id);
        } else {
            queryWrapper.eq(SysRole::getName, name);
        }
        return sysRoleMapper.selectList(queryWrapper).size() <= 0;
    }
}
