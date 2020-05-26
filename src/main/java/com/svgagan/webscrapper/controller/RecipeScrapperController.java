package com.svgagan.webscrapper.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scrapping-recipes")
public class RecipeScrapperController {

    /**
    *
    * websiteUrl:https://www.yummly.com/cuisines
    * baseUrl:https://www.yummly.com
    * */
    @GetMapping
    public String initiateWebScrapping(@RequestParam final String websiteUrl, @RequestParam final String baseUrl) {
        String result;
        try{
            // Here we create a document object and use JSoup to fetch the website
            Document document = Jsoup.connect(websiteUrl).get();

            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.println("Title: "+document.title());

            // Get the list of recipes
            Elements allCuisines = document.getElementsByClass("browse-recipes-title");

            List<String> cuisinesLinks = new ArrayList<>();
            for (Element cuisine : allCuisines) {
                Elements anchorTags = cuisine.getElementsByTag("a");
                for (Element link : anchorTags) {
                    String cuisineUrl = link.attr("href");
                    cuisinesLinks.add(baseUrl+cuisineUrl);
                }
            }
            System.out.println("Total Cuisines Links Available: "+cuisinesLinks.size());
            this.searchWebsite(cuisinesLinks, baseUrl);
            result =  "Success";
        } catch (Exception ex){
            ex.printStackTrace();
            result =  "Failure";
        }
        return result;
    }

    private void searchWebsite(List<String> cuisinesLinks, String baseUrl){
        try{
            List<String> dishes = new ArrayList<>();
            for (String cuisineLink : cuisinesLinks){
                Document cuisinesDocument = Jsoup.connect(cuisineLink).get();
                Elements allDishes = cuisinesDocument.getElementsByClass("recipe-card seo-recipe-card ingredients-static single-recipe");

                List<String> dishesLinks = new ArrayList<>();
                for (Element dish : allDishes) {
                    Elements anchorTags = dish.getElementsByTag("a");
                    for (Element link : anchorTags) {
                        String dishUrl = link.attr("href");
                        dishesLinks.add(baseUrl+dishUrl);
                    }
                }
                System.out.println("For Cuisine "+cuisineLink+", Dishes Links Available are: "+dishesLinks.size());
                dishes.addAll(dishesLinks);
            }
            System.out.println("Total Dishes Links Available: "+dishes.size());
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
