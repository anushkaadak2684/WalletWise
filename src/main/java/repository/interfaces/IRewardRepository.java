package repository.interfaces;

import model.Reward;
import java.util.List;

public interface IRewardRepository {
    void save(Reward reward, int userId);
    Reward findById(int rewardId);
    List<Reward> findByUserId(int userId);
    List<Reward> findAll();
    void update(Reward reward);
    void delete(int rewardId);
}
