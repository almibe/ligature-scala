# ligature-xodus

| Store Name     | Scodec Pseudocode                | Description                      |
|----------------|----------------------------------|----------------------------------|
| Counters       | String -> Long                   | Counter Name -> Counter Value    |
| DataSetToId    | String -> Long                   | Dataset Name -> Dataset Id       |
| IdToDataset    | Long -> String                   | Dataset Id -> Dataset Name       |
| EAV            | Long ~ Long ~ Long ~ Byte ~ Long | Dataset Id ~ Id ~ Id ~ Type ~ Id |
| EVA            | Similar to above                 | Similar to above                 |
| AEV            | Similar to above                 | Similar to above                 |
| AVE            | Similar to above                 | Similar to above                 |
| VEA            | Similar to above                 | Similar to above                 |
| VAE            | Similar to above                 | Similar to above                 |
| IdentifierToId | String -> Long                   | Identifier -> Id                 |
| IdToIdentifier | Long -> String                   | Id -> Identifier                 |
| StringToId     | String -> Long                   | String -> Literal Id             |
| IdToString     | Long -> String                   | Literal Id -> String             |
| BytesToId      | Bytes -> Long                    | Bytes -> Id                      |
| IdToBytes      | Long -> Bytes                    | Id -> Bytes                      |

I don't need lookups for long (and double if I add them) since I can just use the actual values.

| Type Codes | Value      |
|------------|------------|
| 0          | Identifier |
| 1          | String     |
| 2          | Long       |
| 3          | Bytes      |
