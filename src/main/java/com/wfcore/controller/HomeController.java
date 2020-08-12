package com.wfcore.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wfcore.model.Repository;
import com.wfcore.service.CommitsService;
import com.wfcore.service.ContributorsService;
import com.wfcore.service.RepositoryService;

@Controller
public class HomeController {
	@Autowired
	private RepositoryService service;

	@Autowired
	private ContributorsService cService;

	@Autowired
	private CommitsService commitsService;

	@RequestMapping("/")
	public String viewHomePage(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
		List<Repository> repoList = service.findAllRepos(keyword);
		model.addAttribute("repoList", repoList);
		model.addAttribute("keyword", keyword);

		return "index";
	}

	@RequestMapping("/view/{id}")
	public ModelAndView viewInfoPage(@PathVariable(name = "id") String id) {
		ModelAndView mav = new ModelAndView("view");

		Repository repo = service.findById(id);

		// view title page
		String title = repo.getName() + " Analytics";
		mav.addObject("title", title);

		// List of contributors per repository
		List<String> contributors = cService.findContributorsList(repo);
		mav.addObject("contributors", contributors);

		// Impact of contributor based on number of commits out of last100
		Map<String, Integer> contributorsCommit = commitsService.retrieve100LastCommitters(repo);
		mav.addObject("impactList", contributorsCommit);

		// Commit details
		Map<LocalDateTime, String> commitDetailsMap = commitsService.getCommitDetails(repo);
		mav.addObject("commitDetailsMap", commitDetailsMap);
		return mav;
	}
}
