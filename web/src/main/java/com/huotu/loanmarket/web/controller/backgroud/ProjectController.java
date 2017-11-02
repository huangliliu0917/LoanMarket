package com.huotu.loanmarket.web.controller.backgroud;

import com.huotu.loanmarket.common.SysConstant;
import com.huotu.loanmarket.common.ienum.ApplicationMaterialEnum;
import com.huotu.loanmarket.service.entity.LoanCategory;
import com.huotu.loanmarket.service.entity.LoanProject;
import com.huotu.loanmarket.service.searchable.ProjectSearchCondition;
import com.huotu.loanmarket.service.service.CategoryService;
import com.huotu.loanmarket.service.service.ProjectService;
import com.huotu.loanmarket.web.base.ApiResult;
import com.huotu.loanmarket.web.base.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author allan
 * @date 26/10/2017
 */
@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(
            @RequestParam(required = false, defaultValue = "0") int projectId,
            Model model
    ) {
        //分类
        List<LoanCategory> categories = categoryService.findAll();
        //申请材料
        ApplicationMaterialEnum[] applicationMaterials = ApplicationMaterialEnum.values();

        model.addAttribute("categories", categories);
        model.addAttribute("applicationMaterials", applicationMaterials);
        LoanProject project = projectService.findOne(projectId);
        if (project == null) {
            project = new LoanProject();
        }

        model.addAttribute("project", project);

        return "project_edit";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            @ModelAttribute(value = "searchCondition") ProjectSearchCondition searchCondition,
            Model model
    ) {
        List<LoanCategory> categories = categoryService.findAll();
        Page<LoanProject> projectPage = projectService.findAll(pageIndex, SysConstant.BACKEND_DEFALUT_PAGE_SIZE, searchCondition);

        model.addAttribute("categories", categories);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("totalRecord", projectPage.getTotalElements());
        model.addAttribute("totalPage", projectPage.getTotalPages());
        model.addAttribute("projects", projectPage.getContent());

        return "project_list";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult edit(LoanProject project) {
        if (project.getLoanId() == null || project.getLoanId() == 0) {
            project.setCreateTime(new Date());
        }
        String[] enableMoneyArray = project.getEnableMoney().split(",");
        project.setMaxMoney(Double.parseDouble(enableMoneyArray[enableMoneyArray.length - 1]));
        projectService.save(project);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 设置成删除
     *
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult delete(int projectId) {
        LoanProject project = projectService.findOne(projectId);
        project.setIsDelete(1);
        projectService.save(project);

        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @RequestMapping(value = "/setHot", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setHot(int isHot, String projectIdsStr) {
        projectService.setHot(isHot, projectIdsStr);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @RequestMapping(value = "/setNew", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult setNew(int isNew, String projectIdsStr) {
        projectService.setNew(isNew, projectIdsStr);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
