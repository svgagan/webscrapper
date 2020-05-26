package com.svgagan.webscrapper.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scrapping")
public class WebScrapperController {

    /**
    *
    * websiteUrl = https://www.codetriage.com/?language=Java
    * */
    @GetMapping
    public String initiateWebScrapping(@RequestParam final String websiteUrl) {
        String result;
        try{
            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect(websiteUrl).get();

            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.println("Title: "+doc.title());

            // Get the list of repositories
            Elements repositories = doc.getElementsByClass("repo-item");

            for (Element repository : repositories) {
                // Extract the title
                String repositoryTitle = repository.getElementsByClass("repo-item-title").text();

                // Extract the number of issues on the repository
                String repositoryIssues = repository.getElementsByClass("repo-item-issues").text();

                // Extract the description of the repository
                String repositoryDescription = repository.getElementsByClass("repo-item-description").text();

                // Get the full name of the repository
                String repositoryGithubName = repository.getElementsByClass("repo-item-full-name").text();

                // The reposiory full name contains brackets that we remove first before generating the valid Github link.
                String repositoryGithubLink = "https://github.com/" + repositoryGithubName.replaceAll("[()]", "");

                // Format and print the information to the console
                System.out.println(repositoryTitle + " - " + repositoryIssues);
                System.out.println("\t" + repositoryDescription);
                System.out.println("\t" + repositoryGithubLink);
                System.out.println("\n");
            }

            result =  "Success";
        } catch (Exception ex){
            ex.printStackTrace();
            result =  "Failure";
        }
        return result;
    }

}
