package com.daken.controller;

import com.daken.domain.ResponseResult;
import com.daken.domain.entity.Link;
import com.daken.domain.vo.LinkInfoVo;
import com.daken.service.ArticleService;
import com.daken.service.LinkService;
import com.daken.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 原本是友链审核，现改为文章的审核管理
 */
@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult list(Link link, Integer pageNum, Integer pageSize){
        return linkService.selectLinkPage(link,pageNum,pageSize);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Link link){
        linkService.save(link);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable Long id){
        Link link = linkService.getById(id);
        LinkInfoVo vo = BeanCopyUtils.copyBean(link, LinkInfoVo.class);
        return ResponseResult.okResult(vo);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable Long id){
        linkService.removeById(id);
        return ResponseResult.okResult();
    }

    @PutMapping("/changeLinkStatus")
    public ResponseResult changeLinkStatus(@RequestBody Link link){
        linkService.updateById(link);
        return ResponseResult.okResult();
    }


}
