package app.dao;

import app.model.Product;
import app.model.User;
import com.mongodb.client.model.Filters;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.Success;
import org.bson.Document;
import rx.Observable;

public class CatalogDao {
    private static final String DATABASE = "catalog";
    private static final String USER_COLLECTION = "users";
    private static final String PRODUCT_COLLECTION = "products";

    private final MongoClient mongoClient;
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> products;

    public CatalogDao() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.users = getCollection(USER_COLLECTION);
        this.products = getCollection(PRODUCT_COLLECTION);
    }

    public Observable<User> getUser(int id) {
        return users.find(Filters.eq("id", id))
                .first()
                .map(User::new)
                .defaultIfEmpty(null);
    }

    public Observable<Success> addUser(User user) {
        return users.insertOne(user.toDocument());
    }

    public Observable<Product> getProducts() {
        return products.find().toObservable().map(Product::new);
    }

    public Observable<Success> addProduct(Product product) {
        return products.insertOne(product.toDocument());
    }

    private MongoCollection<Document> getCollection(String name) {
        return mongoClient.getDatabase(DATABASE).getCollection(name);
    }
}
