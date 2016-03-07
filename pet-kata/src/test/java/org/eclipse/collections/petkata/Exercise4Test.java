/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.petkata;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.collections.petkata.PetType.*;

/**
 * In this set of tests, wherever you see .stream() replace it with an Eclipse Collections alternative
 */
public class Exercise4Test extends PetDomainForKata
{
    @Test
    public void getAgeStatisticsOfPets()
    {
        // Try to use a MutableIntList here instead
        // Hints: flatMap = flatCollect, map = collect, mapToInt = collectInt
        MutableIntList petAges = this.people
                .flatCollect(person -> person.getPets())
                .collect(pet -> pet.getAge())
                .collectInt(i -> i);

        // Try to use an IntSet here instead
        IntSet uniqueAges = petAges.toSet();
        // IntSummaryStatistics is a class in JDK 8 - Try and use it with MutableIntList.forEach()
        IntSummaryStatistics stats = new IntSummaryStatistics();
        petAges.forEach(stats::accept);
        // Is a Set<Integer> equal to an IntSet?
        // Hint: Try IntSets instead of Sets as the factory
        Assert.assertEquals(IntSets.mutable.with(1, 2, 3, 4), uniqueAges);
        // Try to leverage min, max, sum, average from the Eclipse Collections primitive api 
        Assert.assertEquals(stats.getMin(), petAges.min());
        Assert.assertEquals(stats.getMax(), petAges.max());
        Assert.assertEquals(stats.getSum(), petAges.sum());
        Assert.assertEquals(stats.getAverage(), petAges.average(), 0.0);
        Assert.assertEquals(stats.getCount(), petAges.size());
        // Hint: Match = Satisfy
        Assert.assertTrue(petAges.allSatisfy(i -> i > 0));
        Assert.assertFalse(petAges.anySatisfy(i -> i == 0));
        Assert.assertTrue(petAges.noneSatisfy(i -> i < 0));
    }

    @Test
    public void streamsToECRefactor1()
    {
        //find Bob Smith
        Person person =
                this.people
                        .detect(each -> each.named("Bob Smith"));

        //get Bob Smith's pets' names
        String names =
                person.getPets().collect(pet -> pet.getName()).makeString(" & ");

        Assert.assertEquals("Dolly & Spot", names);
    }

    @Test
    public void streamsToECRefactor2()
    {
        // Hint: Try to replace the Map<PetType, Long> with a Bag<PetType>
        Bag<PetType> counts =
                        this.people
                                .flatCollect(Person::getPetTypes)
                                .toBag().asUnmodifiable();
        Assert.assertEquals(2L, counts.occurrencesOf(CAT));
        Assert.assertEquals(2L, counts.occurrencesOf(DOG));
        Assert.assertEquals(2L, counts.occurrencesOf(HAMSTER));
        Assert.assertEquals(1L, counts.occurrencesOf(SNAKE));
        Assert.assertEquals(1L, counts.occurrencesOf(TURTLE));
        Assert.assertEquals(1L, counts.occurrencesOf(BIRD));
    }

    /**
     * The purpose of this test is to determine the top 3 pet types
     */
    @Test
    public void streamsToECRefactor3()
    {
        // Hint: The result of groupingBy/counting can almost always be replaced by a Bag
        // Hint: Look for the API on Bag that might return the top 3 pet types

        MutableList<ObjectIntPair<PetType>> objectIntPairs = this.people
                .flatCollect(Person::getPetTypes)
                .toBag().topOccurrences(3);

        Verify.assertSize(3, objectIntPairs);
        Verify.assertContains(PrimitiveTuples.pair(CAT, 2), objectIntPairs);
        Verify.assertContains(PrimitiveTuples.pair(DOG, 2), objectIntPairs);
        Verify.assertContains(PrimitiveTuples.pair(HAMSTER, 2), objectIntPairs);

    }
}
