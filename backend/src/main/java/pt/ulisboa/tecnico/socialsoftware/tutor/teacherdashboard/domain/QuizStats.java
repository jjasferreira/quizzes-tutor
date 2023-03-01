package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

import javax.persistence.*;


@Entity
public class QuizStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numQuizzes;

    private int uniqueQuizzesSolved;

    private float averageQuizzesSolved;

    public QuizStats() {
    }

    public Integer getId() {
        return id;
    }

    public int getNumQuizzes() {
        return numQuizzes;
    }

    public void setNumQuizzes(int numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public int getUniqueQuizzesSolved() {
        return uniqueQuizzesSolved;
    }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved) {
        this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
}
