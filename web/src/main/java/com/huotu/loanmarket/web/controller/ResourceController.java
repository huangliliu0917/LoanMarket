package com.huotu.loanmarket.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.huotu.loanmarket.common.utils.StringUtilsExt;
import com.huotu.loanmarket.web.base.ApiResult;
import com.huotu.loanmarket.web.base.ResultCodeEnum;
import com.huotu.loanmarket.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * @author allan
 * @date 26/10/2017
 */
@Controller
@RequestMapping(value = "/resource/upload", method = RequestMethod.POST)
public class ResourceController {
    @Autowired
    private StaticResourceService resourceService;

    @RequestMapping("/img")
    @ResponseBody
    public ApiResult upload(@RequestParam(value = "img", required = false) MultipartFile file) throws IOException, URISyntaxException {
        Date now = new Date();
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            throw new FileNotFoundException("未上传任何图片");
        }
        // TODO: 27/10/2017 图片属性校验
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = "loanMarket/" + StringUtilsExt.dateFormat(now, "yyyyMMdd") + "/"
                + StringUtilsExt.dateFormat(now, "yyyyMMddHHmmSS") + "." + prefix;

        URI uri = resourceService.upload(path, file.getInputStream());

        JSONObject responseData = new JSONObject();
        responseData.put("fileUrl", uri);

        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, responseData);
    }
}
