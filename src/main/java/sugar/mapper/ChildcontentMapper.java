package sugar.mapper;

import org.apache.ibatis.annotations.Param;
import sugar.bean.Childcontent;
import sugar.bean.ChildcontentExample;

import java.util.List;

public interface ChildcontentMapper {
    long countByExample(ChildcontentExample example);

    int deleteByExample(ChildcontentExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Childcontent record);

    int insertSelective(Childcontent record);

    List<Childcontent> selectByExample(ChildcontentExample example);

    Childcontent selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Childcontent record, @Param("example") ChildcontentExample example);

    int updateByExample(@Param("record") Childcontent record, @Param("example") ChildcontentExample example);

    int updateByPrimaryKeySelective(Childcontent record);

    int updateByPrimaryKey(Childcontent record);
}
