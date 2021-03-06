package cn.tycoding.scst.system.biz.controller;

import cn.tycoding.scst.common.core.controller.BaseController;
import cn.tycoding.scst.common.log.annotation.Log;
import cn.tycoding.scst.common.core.utils.QueryPage;
import cn.tycoding.scst.common.core.utils.R;
import cn.tycoding.scst.system.api.entity.SysDept;
import cn.tycoding.scst.system.biz.service.SysDeptService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @date 2019-06-03
 */
@RestController
@RequestMapping("/dept")
@Api(value = "SysDeptController", tags = {"部门管理接口"})
public class SysDeptController extends BaseController {

    @Autowired
    private SysDeptService sysDeptService;

    @PostMapping("/list")
    public R list(SysDept dept, QueryPage queryPage) {
        return new R<>(super.getData(sysDeptService.list(dept, queryPage)));
    }

    @GetMapping("/tree")
    public R<List> tree() {
        return new R<>(sysDeptService.tree());
    }

    @GetMapping("/{id}")
    public R<SysDept> findById(@PathVariable Long id) {
        if (id == null || id == 0) {
            return new R<>();
        } else {
            return new R<>(sysDeptService.getById(id));
        }
    }

    @Log("添加部门")
    @PostMapping
    public R add(@RequestBody SysDept dept) {
        sysDeptService.add(dept);
        return new R();
    }

    @Log("删除部门")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        sysDeptService.delete(id);
        return new R();
    }

    @Log("更新部门")
    @PutMapping
    public R edit(@RequestBody SysDept dept) {
        sysDeptService.update(dept);
        return new R();
    }

    @GetMapping("/checkName/{name}/{id}")
    public R<Boolean> checkName(@PathVariable("name") String name, @PathVariable("id") String id) {
        return new R<>(sysDeptService.checkName(name, id));
    }
}
