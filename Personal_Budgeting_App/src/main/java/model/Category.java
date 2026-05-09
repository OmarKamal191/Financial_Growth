package model;

public class Category {
    private int categoryId;
    private int userId;
    private String name;

    public Category() {}

    public Category(int categoryId, int userId, String name) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
    }

    // Getters and Setters
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}