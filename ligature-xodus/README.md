# ligature-xodus

Below is table showing how Ligature is stored in Xodus.
Note that Identifier is a Ligature Identifier and ID is an internal ID that users will never see,
and is only used for internal references.

| Store Name     | Scodec Pseudocode                | Description                       |
|----------------|----------------------------------|-----------------------------------|
| Counters       | String -> Long                   | Counter Name -> Counter Value     |
| DataSetToId    | String -> Long                   | Dataset Name -> Dataset Id        |
| IdToDataset    | Long -> String                   | Dataset Id -> Dataset Name        |
| EAV            | Long ~ Long ~ Long ~ Byte ~ Long | Dataset Id ~ Id ~ Id ~ Type ~ Id  |
| EVA            | Similar to above                 | Similar to above                  |
| AEV            | Similar to above                 | Similar to above                  |
| AVE            | Similar to above                 | Similar to above                  |
| VEA            | Similar to above                 | Similar to above                  |
| VAE            | Similar to above                 | Similar to above                  |
| IdentifierToId | Long ~ String -> Long            | Dataset Id ~ Identifier -> Id     |
| IdToIdentifier | Long ~ Long -> String            | Dataset Id ~ Id -> Identifier     |
| StringToId     | Long ~ String -> Long            | Dataset Id ~ String -> Literal Id |
| IdToString     | Long ~ Long -> String            | Dataset Id ~ Literal Id -> String |
| BytesToId      | Long ~ Bytes -> Long             | Dataset Id ~ Bytes -> Id          |
| IdToBytes      | Long ~ Long -> Bytes             | Dataset Id ~ Id -> Bytes          |

I don't need lookups for long (and double if I add them) since I can just use the actual values.

| Type Codes | Value      |
|------------|------------|
| 0          | Identifier |
| 1          | String     |
| 2          | Long       |
| 3          | Bytes      |
