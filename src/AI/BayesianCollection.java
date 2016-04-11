package AI;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class BayesianCollection<T, K> extends Categorizer<T, K> {
    private float featuresProbabilityProduct(Collection<T> features, K category) {
        float product = 1.0f;
        for (T feature : features)
            product *= this.featureWeighedAverage(feature, category);
        return product;
    }

    private float categoryProbability(Collection<T> features, K category) {
        return ((float) this.categoryCount(category) 
                / (float) this.getCategoriesTotal())
                * featuresProbabilityProduct(features, category);
    }

    private SortedSet<CategoryCollection<T, K>> categoryProbabilities(Collection<T> features) {
        SortedSet<CategoryCollection<T, K>> probabilities = new TreeSet<CategoryCollection<T, K>>(new Comparator<CategoryCollection<T, K>>() {
            public int compare(CategoryCollection<T, K> o1, CategoryCollection<T, K> o2) {
                int toReturn = Float.compare(
                o1.getProbability(), o2.getProbability());
                if ((toReturn == 0) && !o1.getCategory().equals(o2.getCategory())) 
                    toReturn = -1;
                return toReturn;
            }
        });
        for (K category : this.getCategories()) 
            probabilities.add(new CategoryCollection<T, K>(features, category, this.categoryProbability(features, category)));
        return probabilities;
    }

    public CategoryCollection<T, K> classify(Collection<T> features) {
        SortedSet<CategoryCollection<T, K>> probabilites = this.categoryProbabilities(features);
        if (probabilites.size() > 0)
            return probabilites.last();
        return null;
    }

    public Collection<CategoryCollection<T, K>> classifyDetailed(Collection<T> features) {
        return this.categoryProbabilities(features);
    }
}
