package repository.interfaces;

import model.SavingsGoal;
import java.util.List;

public interface ISavingsGoalRepository {
    void save(SavingsGoal goal, int walletId);
    SavingsGoal findById(int goalId);
    List<SavingsGoal> findByWalletId(int walletId);
    List<SavingsGoal> findAll();
    void update(SavingsGoal goal);
    void delete(int goalId);
}
