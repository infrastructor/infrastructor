package io.infrastructor.core.utils

import org.junit.Test

public class FilteringUtilsTest {
    @Test
    public void matchTags() {
        assert true  == FilteringUtils.match(['a', 'b', 'c']) { 'a' && 'b' } 
        assert false == FilteringUtils.match(['a', 'b', 'c']) { 'a' && 'b' && !'c' } 
        assert true  == FilteringUtils.match(['a', 'b', 'c']) { 'c' } 
        assert false == FilteringUtils.match(['a', 'b', 'c']) { 'e' } 
    }
    
    @Test
    public void matchEmptyTags() {
        assert false == FilteringUtils.match([]) { 'a' } 
        assert false == FilteringUtils.match([]) { 'a' && 'b' } 
        assert true  == FilteringUtils.match([]) { !'a' && !'b' } 
    }
}

