package sugar.mapper;

import org.apache.ibatis.annotations.Param;
import sugar.bean.Peoplelist;
import sugar.bean.PeoplelistExample;

import java.util.List;

public interface PeoplelistMapper {
    long countByExample(PeoplelistExample example);

    int deleteByExample(PeoplelistExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Peoplelist record);

    int insertSelective(Peoplelist record);

    List<Peoplelist> selectByExample(PeoplelistExample example);

    Peoplelist selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Peoplelist record, @Param("example") PeoplelistExample example);

    int updateByExample(@Param("record") Peoplelist record, @Param("example") PeoplelistExample example);

    int updateByPrimaryKeySelective(Peoplelist record);

    int updateByPrimaryKey(Peoplelist record);
}