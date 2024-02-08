## method name query

| keyword | sample                          | SQL                                                                                                                        |
|:--------|:--------------------------------|:---------------------------------------------------------------------------------------------------------------------------|
| And     | findByLastnameAndFirstname      | ... where lastname = ? and firstname = ?                                                                                   |
| Or      | findByLastnameOrFirstname       | ... where lastname = ? or firstname = ?                                                                                    |
| Is, Eq  | findByLastnameEq/findByLastname | … where lastname = ?                                                                                                       |
| not     | findByLastnameNot               | … where lastname != ?                                                                                                      |
| Between | findByStartDateBetween          | … where startDate between  ? and ?                                                                                 |
| Lt      | findByStartDateLt               | … where startDate > ?                                                                                              |
| Lte     | findByStartDateLte              | … where startDate >= ?                                                                                             |
| Gt      | findByStartDateGr               | … where startDate > ?                                                                                                 |
| Gte     | findByStartDateGte              | … where startDate >= ?                                                                                              |
| After   | findByStartDateAfter            | … where startDate > ?                                                                                                |
| Before  | findByStartDateBefore           | … where startDate < ?                                                                                                |
| Like    | findByFirstnameLike             | … where firstname like ?                                                                                             |
| NotLike | findByFirstnameNotLike          | … where firstname not like ?                                                                                      |
| OrderBy | findByAgeOrderByLastnameDesc    | … where age = ? order by lastname desc                                                                                 |
| in      | findByAgeIn                     | … where age  in (?,?)    |
| NotIn   | findByAgeNotIn                  | …  where age  not in (?,?) |