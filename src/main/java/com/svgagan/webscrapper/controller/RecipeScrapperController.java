package com.svgagan.webscrapper.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/scrapping-recipes")
public class RecipeScrapperController {

    /**
    *
    * websiteUrl:https://www.yummly.com/cuisines
    * baseUrl:https://www.yummly.com
    * */
    @GetMapping("/cuisines")
    public List<String> initiateWebScrapping(@RequestParam final String websiteUrl, @RequestParam final String baseUrl) {
        List<String> cuisinesLinks = new ArrayList<>();
        try{
            // Here we create a document object and use JSoup to fetch the website
            Document document = Jsoup.connect(websiteUrl).get();

            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.println("Title: "+document.title());

            // Get the list of recipes
            Elements allCuisines = document.getElementsByClass("browse-recipes-title");


            for (Element cuisine : allCuisines) {
                Elements anchorTags = cuisine.getElementsByTag("a");
                for (Element link : anchorTags) {
                    String cuisineUrl = link.attr("href");
                    cuisinesLinks.add(baseUrl+cuisineUrl);
                }
            }
            System.out.println("Total Cuisines Links Available: "+cuisinesLinks.size());
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return cuisinesLinks;
    }

    @GetMapping("/dishes-links")
    public Set<String> collectDishesLinks(@RequestParam final String cuisinesLinks, @RequestParam final String baseUrl){
        Set<String> dishes = new HashSet<>();
        try{
            for (String cuisineLink : Arrays.asList(cuisinesLinks)){
                Document cuisinesDocument = Jsoup.connect(cuisineLink).get();
                Elements allDishes = cuisinesDocument.getElementsByClass("recipe-card seo-recipe-card ingredients-static single-recipe");

                for (Element dish : allDishes) {
                    String dishUrl = dish.getElementsByClass("link-overlay").attr("href").toString();
                    dishes.add(baseUrl+dishUrl);
                }
            }
            System.out.println("Total Dishes Links Available: "+dishes.size());
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return dishes;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getDishDetails(@RequestParam final String dishesLinks,@RequestParam final String baseUrl) {
        String response = null;
        try {
            for (String dish : Arrays.asList(dishesLinks)){
                Document dishDocument = Jsoup.connect(dish).get();
                String dishName = dishDocument.getElementsByClass("recipe-title font-bold h2-text primary-dark").text();

                // Ingredients count
                Elements ingredientsUnits = dishDocument.getElementsByClass("recipe-summary-item  h2-text");
                String ingredientsCount = ingredientsUnits.first().getElementsByClass("value font-light h2-text").text();

                //Total Time to cook
                Elements timeUnits = dishDocument.getElementsByClass("recipe-summary-item unit h2-text");
                String time = timeUnits.first().getElementsByClass("value font-light h2-text").text();

                //Calories
                Elements caloriesUnits = dishDocument.getElementsByClass("recipe-summary-item nutrition h2-text");
                String calorie = caloriesUnits.first().getElementsByClass("value font-light h2-text").text();

                //Images
                String image = dishDocument.getElementsByClass("recipe-details-image").first().getElementsByTag("img").attr("src");

                //Tags
                Elements tags = dishDocument.getElementsByClass("recipe-tag micro-text font-bold");
                String cuisineName = null;
                for (Element tag : tags){
                    String title = tag.getElementsByTag("li").attr("title").toString();
                    String[] titles = title.split(":");
                    if(titles[0].equalsIgnoreCase("Cuisine")){
                        cuisineName = titles[1];
                    }
                }

                //Directions
                Elements directionsElement = dishDocument.getElementsByClass("wrapper directions-wrapper");
                /*Elements directionsList = directionsElement.first().getElementsByTag("li");
                Map<String, String> directions = new HashMap<>();
                int count=1;
                for (Element direction : directionsList){
                    String step = "step-";
                    directions.put(step+count, direction.getElementsByClass("step").text());
                    count++;
                }*/

                response = "DishName:"+dishName+", CuisineName:"+ cuisineName+", IngredientsCount:"+ingredientsCount+", Time:"+time+", Calorie:"+calorie+", ImageId:"+image;
                System.out.println(response);//+", TotalSteps:"+directions.size());

            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return response;
    }
}
